package com.example.wallpaperapp.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserStore {
    private const val PREF = "users_prefs"
    private const val KEY_USERS = "users_json"
    private val gson = Gson()

    fun registerUser(context: Context, username: String, password: String): Boolean {
        val users = getAllUsers(context).toMutableList()
        val exists = users.any { it.username.equals(username, ignoreCase = true) }
        if (exists) return false

        users.add(User(username = username, password = password))
        saveUsers(context, users)
        return true
    }

    fun validateUser(context: Context, username: String, password: String): Boolean {
        return getAllUsers(context).any {
            it.username.equals(username, ignoreCase = true) && it.password == password
        }
    }

    fun getAllUsers(context: Context): List<User> {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USERS, null) ?: return emptyList()
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveUsers(context: Context, users: List<User>) {
        val json = gson.toJson(users)
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USERS, json)
            .apply()
    }
}
