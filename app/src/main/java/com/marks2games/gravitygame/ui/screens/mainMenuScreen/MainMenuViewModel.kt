package com.marks2games.gravitygame.ui.screens.mainMenuScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.core.data.SharedPreferencesRepository
import com.marks2games.gravitygame.core.domain.authentication.AnonymousSign
import com.marks2games.gravitygame.core.domain.authentication.GoogleSign
import dagger.hilt.android.lifecycle.HiltViewModel
import io.sentry.Sentry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri


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
        if (!hasSignIn) {
            val user = auth.currentUser
            if (user == null) {
                showSignInDialog(true)
            } else if(user.isAnonymous && !mainMenuUiStates.value.alreadySignAsGuest){
                showSignInDialog(true)
            }
        } else {
            viewModelScope.launch {
                getUserImage()
            }
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
            getUserImage()
            showSignInDialog(false)
        }
    }

    private suspend fun getUserImage(){
        withContext(Dispatchers.Main){
            val userImage = auth.currentUser?.photoUrl
            _mainmenuUiState.update { state ->
                state.copy(
                    userImage = userImage
                )
            }
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
        if (openApp(context, "com.facebook.katana", fbUrl.toUri())) {
            return
        }
        if (openApp(context, "com.facebook.lite", fbUrl.toUri())) {
            return
        }
        openInBrowser(context, fbUrl.toUri(), R.string.errorToOpenLink)
    }

    fun openDiscord(context: Context){
        val discordUrl = context.getString(R.string.discordInviteLink)
        openLink(context, "com.discord", discordUrl, R.string.errorToOpenLink)
    }

    fun openBuyMeACoffeeLink(context: Context){
        val buyMeACoffeeUrl = context.getString(R.string.buyMeACoffeeLink)
        openLink(context, "app.buymeacoffee", buyMeACoffeeUrl, R.string.errorToOpenLink)
    }

    fun openEmail(context: Context){
        val emailAddress = context.getString(R.string.email)
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$emailAddress".toUri()
            //putExtra(Intent.EXTRA_SUBJECT, "Subject of mail") // I can define a subject of the mail
            //putExtra(Intent.EXTRA_TEXT, "Body of email") // I can define even a text of the mail
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Sentry.captureException(e)
            Toast.makeText(context, context.getString(R.string.errorToOpenMail), Toast.LENGTH_LONG).show()
        }
    }

    private fun openLink(context: Context, appPackage: String?, url: String, errorMessageId: Int) {
        val uri = url.toUri()
        if (appPackage != null && openApp(context, appPackage, uri)) {
            return
        }
        openInBrowser(context, uri, errorMessageId)
    }

    private fun openApp(context: Context, packageName: String, uri: Uri): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setPackage(packageName)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Sentry.captureException(e)
            false
        }
    }

    private fun openInBrowser(context: Context, uri: Uri, errorMessageId: Int) {
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(errorMessageId), Toast.LENGTH_LONG).show()
            Sentry.captureException(e)
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