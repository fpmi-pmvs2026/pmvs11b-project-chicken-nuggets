package com.example.movies.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.GenresRepository
import com.example.movies.model.GenreDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface GenresUiState {
    data class Success(val genres: List<GenreDTO>) : GenresUiState
    object Error : GenresUiState
    object Loading : GenresUiState
}

class GenresViewModel(
    private val genresRepository: GenresRepository
) : ViewModel() {
    private val _genresUiState = MutableStateFlow<GenresUiState>(GenresUiState.Loading)
    val genresUiState: StateFlow<GenresUiState> = _genresUiState

    init {
        getGenres()
    }

    fun getGenres() {
        viewModelScope.launch {
            _genresUiState.value = GenresUiState.Loading
            _genresUiState.value = try {
                GenresUiState.Success(genresRepository.getGenres())

                //TODO: Make right genre list with img path
            } catch (e: IOException) {
                Log.e("GenresViewModel", "Network error while loading genres", e)
                GenresUiState.Error
            } catch (e: HttpException) {
                Log.e("GenresViewModel", "HTTP error while loading genres: ${e.code()}", e)
                GenresUiState.Error
            } catch (e: Exception) {
                Log.e("GenresViewModel", "Unexpected error", e)
                GenresUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val genresRepository = application.container.genresRepository
                GenresViewModel(genresRepository = genresRepository)
            }
        }
    }
}