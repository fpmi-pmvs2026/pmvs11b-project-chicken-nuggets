package com.example.movies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.model.Movie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _searchResult = MutableStateFlow<List<Movie>>(emptyList())
    val searchResult: StateFlow<List<Movie>> = _searchResult.asStateFlow()

    private var searchJob: Job? = null

    fun searchMovies(query: String) {
        searchJob?.cancel() // Отменяем предыдущий запрос, если он ещё не выполнился
        
        if (query.isBlank()) {
            _searchResult.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(1000) // Пауза в 1 секунду
            
            try {
                val movies = moviesRepository.searchMovies(query)
                _searchResult.value = movies
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                SearchViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}
