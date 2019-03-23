package com.android.me.smack.controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.me.smack.R
import com.android.me.smack.service.AuthService
import com.android.me.smack.service.UserDataService
import com.android.me.smack.util.COLOR_BOUND
import com.android.me.smack.util.DRAWABLE
import com.android.me.smack.util.IMAGE_BOUND
import com.android.me.smack.util.THEME_BOUND
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(THEME_BOUND)
        val avatar = random.nextInt(IMAGE_BOUND)
        userAvatar = if (color == 0) {
            "light$avatar"
        } else {
            "dark$avatar"
        }
        val resourceId = resources.getIdentifier(userAvatar, DRAWABLE, packageName)
        createAvatarImgView.setImageResource(resourceId)
    }

    fun generateColorClicked(view: View) {
        val random = Random()
        val red = random.nextInt(COLOR_BOUND)
        val green = random.nextInt(COLOR_BOUND)
        val blue = random.nextInt(COLOR_BOUND)
        createAvatarImgView.setBackgroundColor(Color.rgb(red, green, blue))

        val savedRed = red.toDouble()/COLOR_BOUND
        val savedGreen = green.toDouble()/COLOR_BOUND
        val savedBlue = blue.toDouble()/COLOR_BOUND
        avatarColor = "[$savedRed, $savedGreen, $savedBlue, 1]"
    }

    fun createUserClicked(view: View) {
        val userName = createUserNameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(this, userName, email, userAvatar, avatarColor) { createSuccess ->
                            if (createSuccess) {
                                println(UserDataService.avatarName)
                                println(UserDataService.avatarColor)
                                println(UserDataService.name)
                                finish()
                            } else {

                            }
                        }
                    } else {

                    }
                }
            } else {

            }
        }
    }

}
