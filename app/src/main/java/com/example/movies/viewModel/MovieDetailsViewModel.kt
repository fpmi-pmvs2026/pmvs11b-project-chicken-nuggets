package com.example.movies.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.model.MovieDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface MovieDetailsUiState {
    data class Success(val movieDetails: MovieDetails) : MovieDetailsUiState
    object Error : MovieDetailsUiState
    object Loading : MovieDetailsUiState
}

sealed interface PlayerUiState {
    object Loading : PlayerUiState
    data class Success(val url: String) : PlayerUiState
    object Error : PlayerUiState
}

class MovieDetailsViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val _movieDetailsUiState =
        MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val movieDetailsUiState: StateFlow<MovieDetailsUiState> = _movieDetailsUiState

    fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            _movieDetailsUiState.value = MovieDetailsUiState.Loading
            _movieDetailsUiState.value = try {
                MovieDetailsUiState.Success(
                    moviesRepository.getMovieDetails(movieId = movieId.toInt())
                )
            } catch (e: IOException) {
                MovieDetailsUiState.Error
            } catch (e: HttpException) {
                MovieDetailsUiState.Error
            }
        }
    }

    private val _playerUiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val playerUiState: StateFlow<PlayerUiState> = _playerUiState

    fun loadPlayer(imdbId: String) {
        viewModelScope.launch {
            _playerUiState.value = PlayerUiState.Loading
            try {
                val url = moviesRepository.getMoviePlayerUrl(imdbId = imdbId)
                Log.d("MovieDetailsViewModel", "Loading player for imdbId: $imdbId, URL: $url")
                if (url.isNullOrEmpty()) {
                    Log.w("MovieDetailsViewModel", "Player URL is null or empty for imdbId: $imdbId")
                    _playerUiState.value = PlayerUiState.Error
                } else {
                    _playerUiState.value = PlayerUiState.Success(url)
                }
            } catch (e: Exception) {
                Log.e("MovieDetailsViewModel", "Error loading player URL for imdbId: $imdbId", e)
                _playerUiState.value = PlayerUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                MovieDetailsViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}