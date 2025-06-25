package com.insfinal.bookdforall.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

// Ubah dari class menjadi object
object SessionManager { // <--- PERUBAHAN: dari 'class' menjadi 'object'

    private const val PREF_NAME = "UserSession"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_AUTH_TOKEN = "authToken"
    private const val KEY_IS_ADMIN = "isAdmin"

    // Inisialisasi SharedPreferences dan Editor harus dilakukan di init
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    // Metode inisialisasi yang akan dipanggil di App.kt
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    fun createLoginSession(authToken: String, isAdmin: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_AUTH_TOKEN, authToken)
        editor.putBoolean(KEY_IS_ADMIN, isAdmin)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        // Cek apakah prefs sudah diinisialisasi
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Call SessionManager.init(context) first.")
            return false
        }
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getAuthToken(): String? {
        // Cek apakah prefs sudah diinisialisasi
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Call SessionManager.init(context) first.")
            return null
        }
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun isAdmin(): Boolean {
        // Cek apakah prefs sudah diinisialisasi
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Call SessionManager.init(context) first.")
            return false
        }
        return prefs.getBoolean(KEY_IS_ADMIN, false)
    }

    fun logoutUser() {
        if (!this::prefs.isInitialized) {
            Log.e("SessionManager", "SessionManager not initialized. Cannot logout.")
            return
        }
        editor.clear() // Ini membersihkan semua data di SharedPreferences
        editor.apply()
        Log.d("SessionManager", "User logged out. SharedPreferences cleared.") // Tambahkan log
    }
}