package com.example.movies.data

import com.example.movies.R

data class AppLanguages(
    val languageCode: String,
    val language: String

    //val iconRes: id
)

val supportedLanguages = listOf(
    AppLanguages("ru", "Русский"),
    AppLanguages("en", "English"),
    AppLanguages("de", "Deutsch")
)

enum class TextScale(val labelResId: Int, val scaleFactor: Float) {
    SMALL(R.string.text_scale_small, 0.7f),
    MEDIUM(R.string.text_scale_medium, 1.0f),
    BIG(R.string.text_scale_big, 1.3f);
}

enum class Theme {
    LIGHT,
    DARK
}
