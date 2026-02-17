package com.example.wallpaperapp.data.local

import android.content.Context

object SettingsStore {
    private const val PREF = "settings_prefs"
    private const val KEY_NSFW = "nsfw_enabled"
    private const val KEY_SAVE_GALLERY = "save_to_gallery"
    private const val KEY_AUTO_DOWNLOAD = "auto_download"

    fun setNsfw(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_NSFW, enabled)
            .apply()
    }

    fun isNsfw(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_NSFW, false)
    }

    fun setSaveToGallery(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SAVE_GALLERY, enabled)
            .apply()
    }

    fun getSaveToGallery(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_SAVE_GALLERY, false)
    }

    fun setAutoDownload(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_DOWNLOAD, enabled)
            .apply()
    }

    fun getAutoDownload(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_DOWNLOAD, false)
    }
}
