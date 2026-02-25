package com.example.wallpaperapp.data.local

import android.content.Context

object SettingsStore {
    private const val PREF = "settings_prefs"
    private const val KEY_THEME_DARK = "theme_dark"
    private const val KEY_SAVE_TO_GALLERY = "save_to_gallery"
    private const val KEY_AUTO_DOWNLOAD = "auto_download"
    private const val KEY_NSFW_MODE = "nsfw_mode"
    private const val KEY_SAFE_SEARCH = "safe_search"
    private const val KEY_BLUR_PREVIEW = "blur_preview"

    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_THEME_DARK, false)
    }

    fun setDarkMode(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_THEME_DARK, value)
            .apply()
    }

    fun isSaveToGalleryEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_SAVE_TO_GALLERY, false)
    }

    fun setSaveToGallery(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SAVE_TO_GALLERY, value)
            .apply()
    }

    fun isAutoDownloadEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_AUTO_DOWNLOAD, false)
    }

    fun setAutoDownload(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AUTO_DOWNLOAD, value)
            .apply()
    }

    fun isNsfwEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_NSFW_MODE, false)
    }

    fun setNsfwEnabled(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_NSFW_MODE, value)
            .apply()
    }

    fun isSafeSearchEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_SAFE_SEARCH, true)
    }

    fun setSafeSearch(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_SAFE_SEARCH, value)
            .apply()
    }

    fun isBlurPreviewEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getBoolean(KEY_BLUR_PREVIEW, true)
    }

    fun setBlurPreview(context: Context, value: Boolean) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BLUR_PREVIEW, value)
            .apply()
    }

    fun resetAll(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
