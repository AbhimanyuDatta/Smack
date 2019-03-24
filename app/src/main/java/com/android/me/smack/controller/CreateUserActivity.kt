package com.android.me.smack.controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.android.me.smack.R
import com.android.me.smack.service.AuthService
import com.android.me.smack.service.UserDataService
import com.android.me.smack.util.*
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
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
        enableSpinner(true)
        val userName = createUserNameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        if (userName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            AuthService.registerUser(email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(userName, email, userAvatar, avatarColor) { createSuccess ->
                                if (createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Make sure all fields are filled in.", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    private fun errorToast() {
        enableSpinner(false)
        Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
    }

    private fun enableSpinner(enable: Boolean) {
        createSpinner.visibility = if (enable) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createAvatarImgView.isEnabled = !enable
        backgroundColorBtn.isEnabled = !enable
    }

}
