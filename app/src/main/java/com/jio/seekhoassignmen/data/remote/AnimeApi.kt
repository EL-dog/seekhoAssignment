package com.jio.seekhoassignmen.data.remote

import android.util.Log
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApi {
    @GET("v4/top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): ApiResponse<List<AnimeDto>>

    @GET("v4/anime/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: Int
    ): ApiDetailResponse<AnimeDetailDto>
}