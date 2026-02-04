package com.example.wallpaperapp.data.wallhaven

import retrofit2.http.GET
import retrofit2.http.Query

interface WallhavenApi {

    @GET("search")
    suspend fun getWallpapers(
        @Query("apikey") apiKey: String,
        @Query("sorting") sorting: String = "random",
        @Query("order") order: String,
        @Query("categories") categories: String = "100",
        @Query("ratios") ratios: String = "9x16,9x18,9x20",
        @Query("atleast") atleast: String = "1080x1920",
        @Query("purity") purity: String = "100",
        @Query("seed") seed: String,
        @Query("q") query: String?,
        @Query("page") page: Int = 1
        ): WallhavenResponse

}

