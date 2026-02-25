package com.example.wallpaperapp.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserDataStore {
    private const val PREF = "user_data_prefs"
    private const val KEY = "user_data_json"
    private val gson = Gson()

    fun getUserData(context: Context): UserData {
        val username = AuthStore.getLoggedInUser(context) ?: return emptyUserData()
        val all = getAllUserData(context)
        return all[username] ?: emptyUserData()
    }

    fun toggleLike(context: Context, imageId: String) {
        val username = AuthStore.getLoggedInUser(context) ?: return
        val all = getAllUserData(context)
        val data = all[username] ?: emptyUserData()

        if (data.likedImages.contains(imageId)) {
            data.likedImages.remove(imageId)
        } else {
            data.likedImages.add(imageId)
        }

        all[username] = data
        saveAllUserData(context, all)
    }

    fun isLiked(context: Context, imageId: String): Boolean {
        val username = AuthStore.getLoggedInUser(context) ?: return false
        val all = getAllUserData(context)
        return all[username]?.likedImages?.contains(imageId) == true
    }

    fun addToCollection(context: Context, collectionName: String, imageId: String) {
        val username = AuthStore.getLoggedInUser(context) ?: return
        val all = getAllUserData(context)
        val data = all[username] ?: emptyUserData()

        val set = data.collections.getOrPut(collectionName) { mutableSetOf() }
        set.add(imageId)

        all[username] = data
        saveAllUserData(context, all)
    }

    fun createCollection(context: Context, collectionName: String) {
        val username = AuthStore.getLoggedInUser(context) ?: return
        val all = getAllUserData(context)
        val data = all[username] ?: emptyUserData()

        if (!data.collections.containsKey(collectionName)) {
            data.collections[collectionName] = mutableSetOf()
            all[username] = data
            saveAllUserData(context, all)
        }
    }

    fun getCollections(context: Context): List<String> {
        val username = AuthStore.getLoggedInUser(context) ?: return emptyList()
        val all = getAllUserData(context)
        return all[username]?.collections?.keys?.toList() ?: emptyList()
    }

    fun getCollectionImages(context: Context, collectionName: String): List<String> {
        val username = AuthStore.getLoggedInUser(context) ?: return emptyList()
        val all = getAllUserData(context)
        val data = all[username] ?: return emptyList()
        return data.collections[collectionName]?.toList() ?: emptyList()
    }

    fun isInAnyCollection(context: Context, imageId: String): Boolean {
        val username = AuthStore.getLoggedInUser(context) ?: return false
        val all = getAllUserData(context)
        val data = all[username] ?: return false
        return data.collections.values.any { it.contains(imageId) }
    }

    fun clearAll(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    private fun emptyUserData(): UserData {
        return UserData(
            likedImages = mutableSetOf(),
            collections = mutableMapOf()
        )
    }

    private fun getAllUserData(context: Context): MutableMap<String, UserData> {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, null) ?: return mutableMapOf()
        val type = object : TypeToken<Map<String, UserData>>() {}.type
        val raw: Map<String, UserData> = gson.fromJson(json, type) ?: emptyMap()

        val normalized = mutableMapOf<String, UserData>()
        for ((username, data) in raw) {
            val liked = data.likedImages.toMutableSet()
            val collections = data.collections.mapValues { it.value.toMutableSet() }.toMutableMap()
            normalized[username] = UserData(likedImages = liked, collections = collections)
        }
        return normalized
    }

    private fun saveAllUserData(context: Context, all: Map<String, UserData>) {
        val json = gson.toJson(all)
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, json)
            .apply()
    }
}
