package com.android.me.smack.controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.android.me.smack.R
import com.android.me.smack.service.AuthService
import com.android.me.smack.service.UserDataService
import com.android.me.smack.util.BROADCAST_USER_DATA_CHANGE
import com.android.me.smack.util.DRAWABLE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        hideKeyboard()
        // broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
                nav_drawer_header_include.usernameNavHeader.text = UserDataService.name
                nav_drawer_header_include.userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, DRAWABLE, packageName)
                nav_drawer_header_include.userImgNavHeader.setImageResource(resourceId)
                nav_drawer_header_include.userImgNavHeader.setBackgroundColor(UserDataService.getAvatarColor(UserDataService.avatarColor))
                nav_drawer_header_include.loginBtnNavHeader.text = getString(R.string.logout)
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View) {
        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            usernameNavHeader.text = getString(R.string.login)
            userEmailNavHeader.text = ""
            userImgNavHeader.setImageResource(R.drawable.profiledefault)
            userImgNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = getString(R.string.login)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view: View) {
        if (AuthService.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
                    // perform logic when clicked
                    val nameTextField = dialogView.addChannelNameTxt
                    val descTextField = dialogView.addChannelDescTxt
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    // create channel with channel name and desc
                    hideKeyboard()
                }
                .setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
                    // cancel dialog
                    hideKeyboard()
                }
                .show()
        }
    }

    fun sendMsgBtnClicked(view: View) {

    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
