package com.example.wallpaperapp.data.wallhaven

data class WallhavenWallpaper(
    val id: String,
    val path: String,
    val thumbs: Thumbs
)

data class Thumbs(
    val small: String
)
