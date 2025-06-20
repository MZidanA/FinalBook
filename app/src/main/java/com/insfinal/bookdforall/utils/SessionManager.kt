package com.insfinal.bookdforall.session

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("auth_token", null)
}