package com.android.me.smack.service

import android.content.Context
import android.content.Intent
import android.service.autofill.UserData
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.me.smack.util.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put(EMAIL, email)
        jsonBody.put(PASSWORD, password)
        val requestBody = jsonBody.toString()

        // object expression (anonymous inner class)
        val registerRequest = object : StringRequest(Request.Method.POST, URL_REGISTER, Response.Listener { response ->
            println(response)
            complete(true)
        }, Response.ErrorListener {error ->
            Log.d("ERROR", "Could not register user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=UTF-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put(EMAIL, email)
        jsonBody.put(PASSWORD, password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Request.Method.POST, URL_LOGIN, null, Response.Listener { response ->
            try {
                userEmail = response.getString(USER)
                authToken = response.getString(TOKEN)
                isLoggedIn = true
                complete(true)
            } catch (ex: JSONException) {
                Log.d("JSON", "Unable to parse response: ${ex.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=UTF-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put(NAME, name)
        jsonBody.put(EMAIL, email)
        jsonBody.put(AVATAR_NAME, avatarName)
        jsonBody.put(AVATAR_COLOR, avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Request.Method.POST, URL_CREATE_USER, null, Response.Listener { response ->
            try {
                UserDataService.name = response.getString(NAME)
                UserDataService.email = response.getString(EMAIL)
                UserDataService.avatarName = response.getString(AVATAR_NAME)
                UserDataService.avatarColor = response.getString(AVATAR_COLOR)
                UserDataService.id = response.getString(KEY_ID)
                complete(true)
            } catch (ex: JSONException) {
                Log.d("JSON", "Unable to parse response: ${ex.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not login user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=UTF-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $authToken"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(createRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest = object : JsonObjectRequest(Request.Method.GET, "$URL_GET_USER$userEmail", null, Response.Listener { response ->
            try {
                UserDataService.name = response.getString(NAME)
                UserDataService.email = response.getString(EMAIL)
                UserDataService.avatarName = response.getString(AVATAR_NAME)
                UserDataService.avatarColor = response.getString(AVATAR_COLOR)
                UserDataService.id = response.getString(KEY_ID)

                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "Unable to parse response: ${e.localizedMessage}")
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not find user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=UTF-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $authToken"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(findUserRequest)
    }

}