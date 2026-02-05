package com.example.wallpaperapp.data.local

import android.content.Context
import org.json.JSONObject

object FavoritesStore {

    private const val PREF = "wallpaper_prefs"
    private const val KEY_FAVORITES = "favorites"   // ❤️ heart
    private const val KEY_SAVED = "saved_items"     // 🔖 bookmark
    private const val KEY_DOWNLOADS = "downloads"

    // -------------------------
    // ❤️ FAVORITES (HEART)
    // -------------------------

    fun toggleFavorite(context: Context, id: String): Boolean {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_FAVORITES, mutableSetOf())!!.toMutableSet()

        val added = if (set.contains(id)) {
            set.remove(id)
            false
        } else {
            set.add(id)
            true
        }

        prefs.edit().putStringSet(KEY_FAVORITES, set).apply()
        return added
    }

    fun isFavorite(context: Context, id: String): Boolean {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORITES, emptySet())!!.contains(id)
    }

    // -------------------------
    // 🔖 SAVED (BOOKMARK)
    // -------------------------

    fun toggleSaved(context: Context, id: String): Boolean {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_SAVED, mutableSetOf())!!.toMutableSet()

        val added = if (set.contains(id)) {
            set.remove(id)
            false
        } else {
            set.add(id)
            true
        }

        prefs.edit().putStringSet(KEY_SAVED, set).apply()
        return added
    }

    fun isSaved(context: Context, id: String): Boolean {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_SAVED, emptySet())!!.contains(id)
    }

    // -------------------------
    // 📥 DOWNLOAD TRACKING
    // -------------------------

    fun addDownload(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val map = prefs.getString(KEY_DOWNLOADS, "{}")!!
        val json = JSONObject(map)

        json.put(id, json.optInt(id, 0) + 1)

        prefs.edit().putString(KEY_DOWNLOADS, json.toString()).apply()
    }

    fun getDownloads(context: Context, id: String): Int {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val map = prefs.getString(KEY_DOWNLOADS, "{}")!!
        val json = JSONObject(map)
        return json.optInt(id, 0)
    }
}
