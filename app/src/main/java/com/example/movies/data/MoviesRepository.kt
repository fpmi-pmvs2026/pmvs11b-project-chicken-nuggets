package com.example.movies.data

import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.movies.MovieWidget
import com.example.movies.model.ChatRequest
import com.example.movies.model.ChatResponse
import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import com.example.movies.network.ExternalIdApiService
import com.example.movies.network.MoviesApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface MoviesRepository {
    suspend fun getMovies(genreId: String, page: Int): List<Movie>
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMovieDetails(movieId: Int): MovieDetails
    suspend fun chatWithAi(request: ChatRequest): ChatResponse
    suspend fun getExternalId(imdbId: String): Int?
    suspend fun getMoviePlayerUrl(imdbId: String): String?
    fun getFavoriteMoviesStream(): Flow<List<Movie>>
    suspend fun insertFavorite(movie: Movie)
    suspend fun insertFavoriteDetails(details: MovieDetails)
    suspend fun deleteFavorite(movie: Movie)
    suspend fun isFavorite(id: Int): Boolean
    suspend fun getLocalMovie(id: Int): Movie?
}

class NetworkMoviesRepository(
    private val movieApiService: MoviesApiService,
    private val externalIdApiService: ExternalIdApiService,
    private val settingsRepository: SettingsRepository,
    private val movieDao: MovieDao
) : MoviesRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun notifyWidget() {
        repositoryScope.launch {
            MovieWidget().updateAll(settingsRepository.context)
        }
    }

    override suspend fun getMovies(genreId: String, page: Int): List<Movie> =
        movieApiService.getMovies(
            genreId = genreId,
            page = page,
            language = settingsRepository.getSavedLanguage()
        ).movies

    override suspend fun searchMovies(query: String): List<Movie> =
        movieApiService.searchMovies(
            query = query,
            language = settingsRepository.getSavedLanguage()
        ).movies

    override suspend fun getMovieDetails(movieId: Int): MovieDetails {
        return try {
            val details = movieApiService.getMovieDetails(
                endpoint = "movie/$movieId",
                language = settingsRepository.getSavedLanguage()
            )
            if (isFavorite(movieId)) {
                movieDao.insertMovie(details.asEntity())
            }
            details
        } catch (e: Exception) {
            val local = movieDao.getMovieById(movieId)
            local?.asDetailsModel() ?: throw e
        }
    }

    override suspend fun chatWithAi(request: ChatRequest): ChatResponse =
        movieApiService.chatWithAi(request)

    override suspend fun getExternalId(imdbId: String): Int? =
        externalIdApiService.getExternalId(imdbId).docs.firstOrNull()?.id

    override suspend fun getMoviePlayerUrl(imdbId: String): String? {
        return null
    }

    override fun getFavoriteMoviesStream(): Flow<List<Movie>> =
        movieDao.getAllFavorites().map { entities -> entities.map { it.asDomainModel() } }

    override suspend fun insertFavorite(movie: Movie) {
        movieDao.insertMovie(movie.asEntity())
        notifyWidget()
    }

    override suspend fun insertFavoriteDetails(details: MovieDetails) {
        movieDao.insertMovie(details.asEntity())
    }

    override suspend fun deleteFavorite(movie: Movie) {
        movieDao.deleteMovieById(movie.id)
        notifyWidget()
    }

    override suspend fun isFavorite(id: Int): Boolean {
        return movieDao.getMovieById(id) != null
    }

    override suspend fun getLocalMovie(id: Int): Movie? {
        return movieDao.getMovieById(id)?.asDomainModel()
    }
}
