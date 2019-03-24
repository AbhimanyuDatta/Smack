package com.android.me.smack.service

import android.graphics.Color
import com.android.me.smack.controller.App
import com.android.me.smack.util.COLOR_BOUND
import com.android.me.smack.util.EMPTY_STR
import java.util.*

object UserDataService {

    var id = EMPTY_STR
    var avatarColor = EMPTY_STR
    var avatarName = EMPTY_STR
    var email = EMPTY_STR
    var name = EMPTY_STR

    fun getAvatarColor(components: String): Int {
        val strippedColor = components.replace("[", EMPTY_STR).replace("]", EMPTY_STR).replace(",", EMPTY_STR)
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
        id = EMPTY_STR
        avatarColor = EMPTY_STR
        avatarName = EMPTY_STR
        email = EMPTY_STR
        name = EMPTY_STR

        App.prefs.authToken = EMPTY_STR
        App.prefs.userEmail = EMPTY_STR
        App.prefs.isLoggedIn = false
    }

}