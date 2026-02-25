package com.example.wallpaperapp.data.local

data class UserData(
    val likedImages: MutableSet<String>,
    val collections: MutableMap<String, MutableSet<String>>
)
