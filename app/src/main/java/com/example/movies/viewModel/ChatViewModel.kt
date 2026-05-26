package com.example.movies.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.movies.MoviesApplication
import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import com.example.movies.model.ChatMessage
import com.example.movies.model.ChatRequest
import com.example.movies.model.RecommendationWithMovie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val moviesRepository: MoviesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("Привет! Я твой кино-ассистент. Какой фильм хочешь посмотреть сегодня?", false))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text, true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val favorites = settingsRepository.getFavoriteIds().toList()
                val response = moviesRepository.chatWithAi(ChatRequest(message = text, favorites = favorites))
                
                val recommendationsWithMovies = response.recommendations.map { rec ->
                    val movieResults = try {
                        moviesRepository.searchMovies(rec.title)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    RecommendationWithMovie(rec, movieResults.firstOrNull())
                }

                val aiMessage = ChatMessage(
                    text = response.explanation + (response.follow_up?.let { "\n\n$it" } ?: ""),
                    isUser = false,
                    recommendations = recommendationsWithMovies
                )
                
                _messages.value = _messages.value + aiMessage
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage("Извини, произошла ошибка при общении с ИИ.", false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoviesApplication)
                val moviesRepository = application.container.moviesRepository
                val settingsRepository = application.container.settingsRepository
                ChatViewModel(moviesRepository = moviesRepository, settingsRepository = settingsRepository)
            }
        }
    }
}
