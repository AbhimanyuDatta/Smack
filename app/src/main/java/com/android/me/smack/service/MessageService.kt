package com.android.me.smack.service

import android.content.Context
import android.util.Log
import com.android.me.smack.controller.App
import com.android.me.smack.model.Channel
import com.android.me.smack.util.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()

    fun getChannels(complete: (Boolean) -> Unit) {
        val channelsRequest = object : JsonArrayRequest(Request.Method.GET, URL_GET_CHANNELS, null, Response.Listener {response ->
            try {
                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val name = channel.getString(NAME)
                    val description = channel.getString(DESCRIPTION)
                    val id = channel.getString(KEY_ID)

                    val newChannel = Channel(name, description, id)
                    this.channels.add(newChannel)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Could not retrieve channels: ${error.localizedMessage}")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return CONTENT_TYPE_JSON
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers[AUTHORIZATION] = "Bearer ${App.prefs.authToken}"
                return headers
            }
        }

        App.prefs.requestQueue.add(channelsRequest)
    }
}