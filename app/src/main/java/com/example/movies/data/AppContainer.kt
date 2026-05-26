package com.example.movies.data

import android.content.Context
import com.example.movies.network.ExternalIdApiService
import com.example.movies.network.MoviesApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

interface AppContainer {
    val moviesRepository: MoviesRepository
    val genresRepository: GenresRepository
    val settingsRepository: SettingsRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://tmdb-proxy-ziqk.onrender.com/"
    private val kinopoiskUrl = "https://api.poiskkino.dev/"

    val json = Json { ignoreUnknownKeys = true }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder().client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl).build()

    private val retrofitKinopoisk: Retrofit = Retrofit.Builder().client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(kinopoiskUrl).build()

    private val retrofitService: MoviesApiService by lazy {
        retrofit.create(MoviesApiService::class.java)
    }

    private val externalIdApiService: ExternalIdApiService by lazy {
        retrofitKinopoisk.create(ExternalIdApiService::class.java)
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context)
    }

    override val genresRepository: GenresRepository by lazy {
        NetworkGenresRepository(retrofitService, settingsRepository)
    }

    private val database: MovieDatabase by lazy {
        MovieDatabase.getDatabase(context)
    }

    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository(
            movieApiService = retrofitService,
            externalIdApiService = externalIdApiService,
            settingsRepository = settingsRepository,
            movieDao = database.movieDao()
        )
    }
}
