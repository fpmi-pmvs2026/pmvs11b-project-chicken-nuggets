package com.example.movies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.glance.appwidget.updateAll
import com.example.movies.MovieWidget
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val moviesRepository: MoviesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _favoritesUiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val favoritesUiState: StateFlow<MoviesUiState> = _favoritesUiState

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            moviesRepository.getFavoriteMoviesStream()
                .catch { _favoritesUiState.value = MoviesUiState.Error }
                .collect { movies ->
                    _favoritesUiState.value = MoviesUiState.Success(movies)
                }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val currentFavorites = moviesRepository.getFavoriteMoviesStream().first()

                for (movie in currentFavorites) {
                    moviesRepository.getMovieDetails(movie.id)
                }
                
                // Уведомляем виджет после обновления данных
                MovieWidget().updateAll(settingsRepository.context)
            } catch (e: Exception) {
                // Ignore errors
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                val settingsRepository = application.container.settingsRepository
                FavoritesViewModel(moviesRepository = moviesRepository, settingsRepository = settingsRepository)
            }
        }
    }
}
