package com.android.me.smack.service

import android.graphics.Color
import com.android.me.smack.controller.App
import com.android.me.smack.util.COLOR_BOUND
import java.util.*

object UserDataService {

    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun getAvatarColor(components: String): Int {
        val strippedColor = components.replace("[", "").replace("]", "").replace(",", "")
        var red = 0
        var green = 0
        var blue = 0
        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            red = (scanner.nextDouble() * COLOR_BOUND).toInt()
            green = (scanner.nextDouble() * COLOR_BOUND).toInt()
            blue = (scanner.nextDouble() * COLOR_BOUND).toInt()
        }

        return Color.rgb(red, green, blue)
    }

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""

        App.prefs.authToken = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
    }

}