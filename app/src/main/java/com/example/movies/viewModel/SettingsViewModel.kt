package com.example.movies.viewModel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.glance.appwidget.updateAll
import com.example.movies.MovieWidget
import com.example.movies.MoviesApplication
import com.example.movies.ReminderManager
import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import com.example.movies.data.TextScale
import com.example.movies.data.Theme
import com.example.movies.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val _selectedLanguage = MutableStateFlow(settingsRepository.getSavedLanguage())
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _selectedScale = MutableStateFlow(settingsRepository.getSavedTextScale())
    val selectedScale: StateFlow<TextScale> = _selectedScale

    private val _selectedTheme = MutableStateFlow(settingsRepository.getSavedTheme())
    val selectedTheme: StateFlow<Theme> = _selectedTheme

    private val _favoriteIds = MutableStateFlow(settingsRepository.getFavoriteIds())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    private val _movieTags = MutableStateFlow<Map<String, String>>(emptyMap())
    val movieTags: StateFlow<Map<String, String>> = _movieTags

    private val _movieRatings = MutableStateFlow<Map<String, Int>>(emptyMap())
    val movieRatings: StateFlow<Map<String, Int>> = _movieRatings

    init {
        loadTagsAndRatings()
    }

    private fun loadTagsAndRatings() {
        val tags = mutableMapOf<String, String>()
        val ratings = mutableMapOf<String, Int>()
        _favoriteIds.value.forEach { id ->
            settingsRepository.getMovieTag(id)?.let { tags[id] = it }
            val rating = settingsRepository.getMovieRating(id)
            if (rating != -1) ratings[id] = rating
        }
        _movieTags.value = tags
        _movieRatings.value = ratings
    }

    fun toggleFavorite(movie: Movie) {
        val currentFavorites = _favoriteIds.value.toMutableSet()
        val idStr = movie.id.toString()
        viewModelScope.launch {
            if (currentFavorites.contains(idStr)) {
                settingsRepository.removeFavorite(movie.id)
                moviesRepository.deleteFavorite(movie)
                currentFavorites.remove(idStr)
                
                // Cancel reminder if it was set
                if (_movieTags.value[idStr] == "Напомнить") {
                    ReminderManager.cancelReminder(settingsRepository.context, movie.id)
                }

                val newTags = _movieTags.value.toMutableMap()
                newTags.remove(idStr)
                _movieTags.value = newTags
            } else {
                settingsRepository.addFavorite(movie.id)
                moviesRepository.insertFavorite(movie)
                currentFavorites.add(idStr)
            }
            _favoriteIds.value = currentFavorites
            // Обновляем виджет немедленно
            MovieWidget().updateAll(settingsRepository.context)
        }
    }

    fun updateMovieTag(id: Int, tag: String?, title: String? = null, releaseDate: String? = null) {
        val idStr = id.toString()
        val oldTag = _movieTags.value[idStr]
        
        settingsRepository.saveMovieTag(idStr, tag)
        val newTags = _movieTags.value.toMutableMap()
        if (tag == null) newTags.remove(idStr) else newTags[idStr] = tag
        _movieTags.value = newTags

        // Handle Reminder scheduling
        if (tag == "Напомнить" && title != null && releaseDate != null) {
            ReminderManager.scheduleReminder(settingsRepository.context, id, title, releaseDate)
        } else if (oldTag == "Напомнить" && tag != "Напомнить") {
            ReminderManager.cancelReminder(settingsRepository.context, id)
        }
        
        viewModelScope.launch {
            // Обновляем виджет, так как он показывает фильмы именно с тегом "Смотрю"
            MovieWidget().updateAll(settingsRepository.context)
        }
    }

    fun updateMovieRating(id: Int, rating: Int) {
        val idStr = id.toString()
        settingsRepository.saveMovieRating(idStr, rating)
        val newRatings = _movieRatings.value.toMutableMap()
        if (rating == -1) newRatings.remove(idStr) else newRatings[idStr] = rating
        _movieRatings.value = newRatings
        // Рейтинг на виджете не отображается, но для порядка можно обновить
    }

    fun selectLanguage(code: String) {
        _selectedLanguage.value = code
        settingsRepository.saveLanguage(code)
        val locales = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun selectTextScale(scale: TextScale) {
        _selectedScale.value = scale
        settingsRepository.saveTextScale(scale)
        val context = settingsRepository.context
        val config = context.resources.configuration
        config.fontScale = scale.scaleFactor
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun selectTheme(theme: Theme) {
        _selectedTheme.value = theme
        settingsRepository.saveTheme(theme)
        when (theme) {
            Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        viewModelScope.launch {
            // Виджет должен менять тему вместе с системой, но принудительное обновление поможет
            MovieWidget().updateAll(settingsRepository.context)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MoviesApplication)
                val settingsRepository = application.container.settingsRepository
                val moviesRepository = application.container.moviesRepository
                SettingsViewModel(
                    settingsRepository = settingsRepository,
                    moviesRepository = moviesRepository
                )
            }
        }
    }
}
