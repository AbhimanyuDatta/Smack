package com.android.me.smack.util

const val DRAWABLE = "drawable"
const val COLOR_BOUND = 255
const val IMAGE_BOUND = 28
const val THEME_BOUND = 2

const val EMAIL = "email"
const val PASSWORD = "password"
const val TOKEN = "token"
const val USER = "user"
const val NAME = "name"
const val AVATAR_NAME = "avatarName"
const val AVATAR_COLOR = "avatarColor"
const val KEY_ID = "_id"
const val AUTHORIZATION = "Authorization"
const val DESCRIPTION = "description"

const val CONTENT_TYPE_JSON = "application/json; charset=UTF-8"

// API endpoints
const val BASE_URL = "http://10.0.2.2:3005/v1"
const val SOCKET_URL = "http://10.0.2.2:3005/"
const val URL_REGISTER = "$BASE_URL/account/register"
const val URL_LOGIN = "$BASE_URL/account/login"
const val URL_CREATE_USER = "$BASE_URL/user/add"
const val URL_GET_USER = "$BASE_URL/user/byEmail/"
const val URL_GET_CHANNELS = "$BASE_URL/channel"

// broadcast
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"