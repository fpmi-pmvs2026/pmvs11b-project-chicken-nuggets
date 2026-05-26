package com.example.movies.network

import com.example.movies.model.PlayerResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PlayerApiService {
    @FormUrlEncoded
    @POST("/cache")
    suspend fun getPlayers(
        @Field("kinopoisk") kinopoiskId: Int,
        @Field("type") type: String = "movie"
    ): List<PlayerResponse>
}