package com.example.movies

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.movies.data.AppContainer
import com.example.movies.data.DefaultAppContainer

class MoviesApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        val lang = container.settingsRepository.getSavedLanguage()
        if (lang.isNotEmpty()) {
            val locales = LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(locales)

        }

        val textScale = container.settingsRepository.getSavedTextScale()
        if (textScale != null) {
            val config = resources.configuration
            config.fontScale = textScale.scaleFactor

            @Suppress("DEPRECATION") resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}