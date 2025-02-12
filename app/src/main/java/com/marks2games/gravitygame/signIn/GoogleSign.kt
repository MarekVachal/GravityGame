package com.marks2games.gravitygame.signIn

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.marks2games.gravitygame.R
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class GoogleSign @Inject constructor(
    private val context: Context,
    private val auth: FirebaseAuth
) {

    private val webClientId = context.getString(R.string.default_web_client_id)
    private val credentialManager = CredentialManager.create(context)

    suspend fun logout(){
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    @SuppressLint("CredentialManagerSignInWithGoogle")
    suspend fun signInWithCredentialManager() {
        try {
            val token = getGoogleIdToken() ?: throw Exception("Google ID token is null")
            authenticateWithFirebase(token)
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

    suspend fun reauthenticateAndDeleteUser() {
        try {
            val googleIdToken = getGoogleIdToken() ?: throw Exception("Google ID token is null")
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuth", "Reauthentication successful")
                        deleteUser()
                    } else {
                        Log.e("FirebaseAuth", "Reauthentication failed", task.exception)
                    }
                }
        } catch (e: Exception) {
            Log.e("GoogleSign", "Reauthentication failed: ${e.message}")
            Sentry.captureException(e)
        }
    }

    private fun deleteUser() {
        auth.currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                        "AppSettings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("hasSignIn", false)
                    editor.apply()
                    Log.d("FirebaseAuth", "User account deleted successfully")
                } else {
                    Log.e("FirebaseAuth", "User deletion failed", task.exception)
                }
            }
    }

    suspend fun linkGuestAccountWithGoogle(updateUserName:()-> Unit, updateUserEmail:() -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val token = getGoogleIdToken()
                val credential = GoogleAuthProvider.getCredential(token, null)

                withContext(Dispatchers.Main) {
                    auth.currentUser?.linkWithCredential(credential)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = task.result?.user
                                if (user != null) {
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.providerData.find { it.providerId == "google.com" }?.displayName)
                                        .setPhotoUri(user.providerData.find { it.providerId == "google.com" }?.photoUrl)
                                        .build()

                                    user.updateProfile(profileUpdates)
                                        .addOnCompleteListener { updateTask ->
                                            if (updateTask.isSuccessful) {
                                                val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                                                    "AppSettings", Context.MODE_PRIVATE
                                                )
                                                val editor = sharedPreferences.edit()
                                                editor.putBoolean("hasSignIn", true)
                                                editor.apply()

                                                updateUserEmail()
                                                updateUserName()
                                            } else {
                                                Log.e("FirebaseAuth", "Updating profile failed", updateTask.exception)
                                            }
                                        }
                                }
                            } else {
                                Log.e("FirebaseAuth", "Linking guest account failed", task.exception)
                            }
                        }
                }
            } catch (e: Exception) {
                Sentry.captureException(e)
            }
        }
    }

    private suspend fun getGoogleIdToken(): String?{
        val rawNonce = UUID.randomUUID().toString()
        val hashedNonce = hashNonce(rawNonce)
        var token: String? = null

        withContext(Dispatchers.IO){
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(webClientId)
                    .setNonce(hashedNonce)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                val credential = result.credential

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    token = googleIdTokenCredential.idToken
                } else {
                    Log.e("GoogleSign", "Unexpected type of credential")
                    token = null
                }

            } catch (e: GetCredentialException) {
                e.message?.let { Log.d("GoogleSign", "Get credential failed: $it") }
                try {
                    val newGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setNonce(hashedNonce)
                        .build()

                    val newRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(newGoogleIdOption)
                        .build()

                    val newResult = credentialManager.getCredential(
                        context = context,
                        request = newRequest
                    )
                    val newCredential = newResult.credential

                    if (newCredential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(newCredential.data)
                        token = googleIdTokenCredential.idToken
                    } else {
                        Log.e("GoogleSign", "Unexpected type of credential")
                        token = null
                    }

                } catch (signupException: GetCredentialException) {
                    Log.e("GoogleSign", "Sign-up flow failed: ${signupException.message}")
                    //Put a code for alternative sign up. For example using mail and password
                    //It probably wants a new screen
                }
            }
        }
        return token
    }

    private fun authenticateWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("FirebaseAuth", "Authentication successful")
                    Log.d("FirebaseAuth", "User UID: ${user?.uid}")
                    Log.d("FirebaseAuth", "User Email: ${user?.email}")
                    Log.d("FirebaseAuth", "User Display Name: ${user?.displayName}")
                } else {
                    Log.e("FirebaseAuth", "Authentication failed", task.exception)
                }
            }
    }

    private fun hashNonce(rawNonce: String): String {
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /*
    private fun signInPlayGamesServices() {
        val signInClient = PlayGames.getGamesSignInClient(activity)

        signInClient.signIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                PlayGames.getPlayersClient(activity).currentPlayer
                    .addOnSuccessListener { player ->
                        val playerName = player.displayName
                        val playerId = player.playerId
                        Log.d("PlayGames", "Player Name: $playerName")
                        Log.d("PlayGames", "Player ID: $playerId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("PlayGames", "Failed to get player details", e.cause)
                    }
            } else {
                Log.e("PlayGames", "Sign-in failed", task.exception)
            }
        }
    }

     */
}

