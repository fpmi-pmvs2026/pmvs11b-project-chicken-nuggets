package com.example.movies.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface MoviesUiState {
    data class Success(val movies: List<Movie>) : MoviesUiState
    object Error : MoviesUiState
    object Loading : MoviesUiState
}

class MoviesViewModel(
    private val moviesRepository: MoviesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _moviesUiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Loading)
    val moviesUiState: StateFlow<MoviesUiState> = _moviesUiState

    private var currentPage = 1
    private var currentGenreId: String = ""
    private var isLoadingMore = false
    private val genreId: String = checkNotNull(savedStateHandle["genreId"])

    init {
        loadMovies()
    }

    fun loadMovies(/*genreId: String*/) {
        if (isLoadingMore) return
        isLoadingMore = true

        viewModelScope.launch {
            val currentState = _moviesUiState.value
            val pageToLoad = if (currentState is MoviesUiState.Success) currentPage + 1 else 1

            try {
                val newMovies = moviesRepository.getMovies(genreId, pageToLoad)

                val updatedMovies = if (currentState is MoviesUiState.Success) {
                    currentState.movies + newMovies
                } else {
                    newMovies
                }

                _moviesUiState.value = MoviesUiState.Success(updatedMovies)
                currentPage = pageToLoad
                currentGenreId = genreId
            } catch (_: IOException) {
                if (currentState !is MoviesUiState.Success) {
                    _moviesUiState.value = MoviesUiState.Error
                }
            } catch (_: HttpException) {
                if (currentState !is MoviesUiState.Success) {
                    _moviesUiState.value = MoviesUiState.Error
                }
            } finally {
                isLoadingMore = false
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                MoviesViewModel(moviesRepository = moviesRepository,
                    this.createSavedStateHandle())
            }
        }
    }
}