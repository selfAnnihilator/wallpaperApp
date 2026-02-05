package com.example.wallpaperapp.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object CollectionsStore {

    private const val PREF = "collections_store"
    private const val KEY = "collections_json"

    private fun getRoot(context: Context): JSONObject {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY, "{}")!!
        return JSONObject(raw)
    }

    private fun saveRoot(context: Context, obj: JSONObject) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, obj.toString())
            .apply()
    }

    // --------------------

    fun createCollection(context: Context, name: String) {
        val root = getRoot(context)
        if (!root.has(name)) {
            root.put(name, JSONArray())
            saveRoot(context, root)
        }
    }

    fun addToCollection(context: Context, name: String, imageUrl: String) {
        val root = getRoot(context)
        val arr = root.optJSONArray(name) ?: JSONArray()

        if (!arr.toList().contains(imageUrl)) {
            arr.put(imageUrl)
        }

        root.put(name, arr)
        saveRoot(context, root)
    }

    fun getCollections(context: Context): List<String> {
        val root = getRoot(context)
        return root.keys().asSequence().toList()
    }

    fun getImages(context: Context, name: String): List<String> {
        val arr = getRoot(context).optJSONArray(name) ?: JSONArray()
        return arr.toList().map { it.toString() }
    }

    private fun JSONArray.toList(): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until length()) list.add(get(i))
        return list
    }
}
