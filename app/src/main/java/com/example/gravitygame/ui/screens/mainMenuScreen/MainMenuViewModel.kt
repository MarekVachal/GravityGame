package com.example.gravitygame.ui.screens.mainMenuScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.gravitygame.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainMenuViewModel : ViewModel() {

    private val _mainmenuUiState = MutableStateFlow(MainMenuUiStates())
    val mainMenuUiStates: StateFlow<MainMenuUiStates> = _mainmenuUiState.asStateFlow()

    fun showMenuList(toShow: Boolean){
        _mainmenuUiState.value = _mainmenuUiState.value.copy(showMenuList = toShow)
    }

    fun openDiscord(context: Context){
        val discordUrl = context.getString(R.string.discordInviteLink)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(discordUrl))

        try {
            intent.setPackage("com.discord")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                intent.setPackage(null)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.errorToOpenLink), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun openBuyMeACoffeeLink(context: Context){
        val buyMeACoffeeUrl = context.getString(R.string.buyMeACoffeeLink)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(buyMeACoffeeUrl))

        try {
            intent.setPackage("app.buymeacoffee")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try{
                intent.setPackage(null)
                context.startActivity(intent)
            } catch (e: Exception){
                Toast.makeText(context, context.getString(R.string.errorToOpenLink), Toast.LENGTH_LONG).show()
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
            Toast.makeText(context, context.getString(R.string.errorToOpenMail), Toast.LENGTH_LONG).show()
        }
    }

    fun openTextDialog(text: Text? = null, toShow: Boolean){
        _mainmenuUiState.value = _mainmenuUiState.value.copy(
            showTextDialog = toShow,
            textToShow = text)
    }

}