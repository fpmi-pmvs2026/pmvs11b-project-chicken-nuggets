package com.example.movies.network

import com.example.movies.data.ApiKey
import com.example.movies.model.ExternalIdResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ExternalIdApiService {
    @GET("v1.4/movie")
    suspend fun getExternalId(
        @Query("externalId.imdb") imdbId: String,
        @Header("X-API-KEY") apiKey: String = ApiKey.KINOPOISK_API_KEY
    ): ExternalIdResponse
}