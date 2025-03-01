package com.marks2games.gravitygame.ui.screens.mainMenuScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.models.SharedPreferencesRepository
import com.marks2games.gravitygame.signIn.AnonymousSign
import com.marks2games.gravitygame.signIn.GoogleSign
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sentry.Sentry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject


@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferencesRepository
): ViewModel() {

    private val _mainmenuUiState = MutableStateFlow(MainMenuUiStates())
    val mainMenuUiStates: StateFlow<MainMenuUiStates> = _mainmenuUiState.asStateFlow()
    private val anonymousSign = AnonymousSign(auth)

    fun showSignInDialog(toShow: Boolean){
        _mainmenuUiState.update { state ->
            state.copy(
                showSignInDialog = toShow
            )
        }
    }


    fun shouldSignIn(){
        val hasSignIn = sharedPreferences.getHasSignIn()
        Log.d("hasSignIn", "Has sign in: ${sharedPreferences.getHasSignIn()}")
        Log.d("hasSignIn", "Already sign as guest: ${mainMenuUiStates.value.alreadySignAsGuest}")
        Log.d("hasSignIn", "Is user anonym: ${auth.currentUser?.isAnonymous}")
        if (!hasSignIn) {
            val user = auth.currentUser
            if (user == null) {
                showSignInDialog(true)
            } else if(user.isAnonymous && !mainMenuUiStates.value.alreadySignAsGuest){
                showSignInDialog(true)
            }
        } else {
            getUserImage()
        }
    }

    fun anonymousSignIn(){
        viewModelScope.launch {
            anonymousSign.signInAnonymously()
        }
        showSignInDialog(false)
        _mainmenuUiState.update { state ->
            state.copy(
                alreadySignAsGuest = true
            )
        }
    }

    fun signInWithGoogle(googleSign: GoogleSign){
        viewModelScope.launch {
            googleSign.signInWithCredentialManager()
            getUserImage().wait()
        }
        showSignInDialog(false)
        sharedPreferences.setHasSignIn(true)
    }

    private fun getUserImage(){
        val userImage = auth.currentUser?.photoUrl
        _mainmenuUiState.update { state ->
            state.copy(
                userImage = userImage
            )
        }
    }

    fun setTextTitle(context: Context): String{
        return when(mainMenuUiStates.value.textToShow){
            Text.ABOUT_US -> context.getString(R.string.aboutUsTitle)
            Text.GAME_RULES -> context.getString(R.string.gameRulesTitle)
            Text.ABOUT_GAME -> context.getString(R.string.aboutGameTitle)
            Text.DONATE -> context.getString(R.string.donateTitle)
        }
    }

    fun setText(context: Context): String{
        return when(mainMenuUiStates.value.textToShow){
            Text.ABOUT_US -> context.getString(R.string.aboutUsText)
            Text.GAME_RULES -> context.getString(R.string.gameRulesText)
            Text.ABOUT_GAME -> context.getString(R.string.aboutGameText)
            Text.DONATE -> context.getString(R.string.donateText)
        }
    }

    fun showMenuList(toShow: Boolean){
        _mainmenuUiState.update { state ->
            state.copy(showMenuList = toShow)
        }
    }

    fun openFacebook(context: Context){
        val fbUrl = context.getString(R.string.facebookLink)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            intent.setPackage("com.facebook.katana")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException){
            try {
                intent.setPackage("com.facebook.lite")
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException){
                try {
                    intent.setPackage(null)
                    context.startActivity(intent)
                } catch (e: Exception){
                    Toast.makeText(context, context.getString(R.string.errorToOpenLink), Toast.LENGTH_LONG).show()
                    Sentry.captureException(e)
                }
            }
        }
    }

    fun openDiscord(context: Context){
        val discordUrl = context.getString(R.string.discordInviteLink)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(discordUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            intent.setPackage("com.discord")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                intent.setPackage(null)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.errorToOpenLink), Toast.LENGTH_LONG).show()
                Sentry.captureException(e)
            }
        }
    }

    fun openBuyMeACoffeeLink(context: Context){
        val buyMeACoffeeUrl = context.getString(R.string.buyMeACoffeeLink)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(buyMeACoffeeUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            intent.setPackage("app.buymeacoffee")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try{
                intent.setPackage(null)
                context.startActivity(intent)
            } catch (e: Exception){
                Toast.makeText(context, context.getString(R.string.errorToOpenLink), Toast.LENGTH_LONG).show()
                Sentry.captureException(e)
            }
        }
    }

    fun openEmail(context: Context){
        val emailAddress = context.getString(R.string.email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$emailAddress")
            //putExtra(Intent.EXTRA_SUBJECT, "Subject of mail") // I can define a subject of the mail
            //putExtra(Intent.EXTRA_TEXT, "Body of email") // I can define even a text of the mail
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Sentry.captureException(e)
            Toast.makeText(context, context.getString(R.string.errorToOpenMail), Toast.LENGTH_LONG).show()
        }
    }

    fun openTextDialog(text: Text = Text.ABOUT_GAME, toShow: Boolean){
        _mainmenuUiState.update { state ->
            state.copy(
                showTextDialog = toShow,
                textToShow = text
            )
        }
    }

}