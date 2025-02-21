package com.marks2games.gravitygame.ui.screens.accountScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.signIn.GoogleSign
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sentry.Sentry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val auth: FirebaseAuth
): ViewModel() {

    private val _accountUiState = MutableStateFlow(AccountUiState())
    val accountUiState: StateFlow<AccountUiState> = _accountUiState.asStateFlow()

    fun updateShowDeleteAccountDialog(toShow: Boolean){
        _accountUiState.update { state ->
            state.copy(
                showDeleteAccountDialog = toShow
            )
        }
    }

    fun showDeleteButton(): Boolean {
        return if(auth.currentUser == null){
            false
        } else if (auth.currentUser?.isAnonymous == true){
            false
        } else {
            true
        }
    }

    fun deleteUserAccount(googleSign: GoogleSign, context: Context){
        setupAuthStateListener(context)
        viewModelScope.launch {
            googleSign.reauthenticateAndDeleteUser()
        }
        updateShowDeleteAccountDialog(false)
    }

    fun updateUserEmail(context: Context){
        val newEmail = if(auth.currentUser == null){
            context.getString(R.string.userLogout)
        } else {
            auth.currentUser?.email ?: context.getString(R.string.userNoEmail)
        }
        _accountUiState.update { state ->
            state.copy(
                userEmail = newEmail
            )
        }
    }

    fun openPrivacyPolicyLink(context: Context){
        val privacyPolicyUri = context.getString(R.string.privacyPolicyLink)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.errorToOpenLink), Toast.LENGTH_LONG).show()
            Sentry.captureException(e)
        }
    }

    fun updateUserName(context: Context){
        val newName = if(auth.currentUser == null){
            context.getString(R.string.userLogout)
        } else if (auth.currentUser?.isAnonymous == true){
            context.getString(R.string.guest)
        } else {
            auth.currentUser?.displayName ?: context.getString(R.string.userNoName)
        }
        _accountUiState.update { state ->
            state.copy(
                userName = newName
            )
        }
    }

    fun setTextOnClickButton(context: Context): String{
        return if(auth.currentUser == null) {
            context.getString(R.string.login)
        } else if (auth.currentUser?.isAnonymous == true){
            context.getString(R.string.createAccount)
        } else {
            context.getString(R.string.logOut)
        }
    }

    fun setClickOnButton(context: Context, googleSign: GoogleSign){
        val user = auth.currentUser
        if (user == null) {
            setupAuthStateListener(context)
            viewModelScope.launch {
                googleSign.signInWithCredentialManager()
            }
        } else if (user.isAnonymous){
            setupAuthStateListener(context)
            viewModelScope.launch {
                googleSign.linkGuestAccountWithGoogle(
                    updateUserName = { updateUserName(context) },
                    updateUserEmail = { updateUserEmail(context) }
                )
            }
        } else {
            setupAuthStateListener(context)
            viewModelScope.launch {
                googleSign.logout()

            }

        }
    }

    private fun setupAuthStateListener(context: Context) {
        val initialUser = auth.currentUser
        val authListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val newUser = auth.currentUser
                if (newUser != initialUser) {
                    updateUserEmail(context)
                    updateUserName(context)
                    auth.removeAuthStateListener(this)
                }
            }
        }
        auth.addAuthStateListener(authListener)
    }
}