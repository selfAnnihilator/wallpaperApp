package com.example.wallpaperapp.data.local

import android.content.Context

object AuthStore {
    private const val PREF = "auth_prefs"
    private const val KEY_LOGGED_IN_USER = "logged_in_user"

    fun isLoggedIn(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .contains(KEY_LOGGED_IN_USER)
    }

    fun login(context: Context, username: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LOGGED_IN_USER, username)
            .apply()
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_LOGGED_IN_USER)
            .apply()
    }

    fun getLoggedInUser(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_LOGGED_IN_USER, null)
    }
}
