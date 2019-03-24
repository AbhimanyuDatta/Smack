package com.android.me.smack.controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import com.android.me.smack.R
import com.android.me.smack.model.Channel
import com.android.me.smack.model.Message
import com.android.me.smack.service.AuthService
import com.android.me.smack.service.MessageService
import com.android.me.smack.service.UserDataService
import com.android.me.smack.util.BROADCAST_USER_DATA_CHANGE
import com.android.me.smack.util.DRAWABLE
import com.android.me.smack.util.EMPTY_STR
import com.android.me.smack.util.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    private lateinit var channelAdapter: ArrayAdapter<Channel>
    private var selectedChannel: Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

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
        setupAdapters()

        // broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        socket.connect()
        socket.on("channelCreated", onNewChannel)

        socket.on("messageCreated", onNewMessage)

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {
                nav_drawer_header_include.usernameNavHeader.text = UserDataService.name
                nav_drawer_header_include.userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, DRAWABLE, packageName)
                nav_drawer_header_include.userImgNavHeader.setImageResource(resourceId)
                nav_drawer_header_include.userImgNavHeader.setBackgroundColor(UserDataService.getAvatarColor(UserDataService.avatarColor))
                nav_drawer_header_include.loginBtnNavHeader.text = getString(R.string.logout)

                MessageService.getChannels {complete ->
                    if (complete) {
                        if (MessageService.channels.count() > 0) {
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        // download messages for channels
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View) {
        if (App.prefs.isLoggedIn) {
            UserDataService.logout()
            usernameNavHeader.text = getString(R.string.login)
            userEmailNavHeader.text = EMPTY_STR
            userImgNavHeader.setImageResource(R.drawable.profiledefault)
            userImgNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = getString(R.string.login)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view: View) {
        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("Add") { _: DialogInterface?, _: Int ->
                    // perform logic when clicked
                    val nameTextField = dialogView.addChannelNameTxt
                    val descTextField = dialogView.addChannelDescTxt
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    // create channel with channel name and desc
                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel") { _: DialogInterface?, _: Int ->
                    // cancel dialog
                }
                .show()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDesc = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDesc, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            val msgBody = args[0] as String
            val channelId = args[2] as String
            val userName = args[3] as String
            val userAvatar = args[4] as String
            val userAvatarColor = args[5] as String
            val id = args[6] as String
            val timeStamp = args[7] as String

            val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
            MessageService.messages.add(newMessage)
            println(newMessage.message)
        }
    }

    fun sendMsgBtnClicked(view: View) {
        if (App.prefs.isLoggedIn && msgTxt.text.isNotBlank() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage", msgTxt.text.toString(), userId, channelId, UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            msgTxt.text.clear()
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
