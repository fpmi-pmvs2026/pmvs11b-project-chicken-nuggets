package com.example.movies.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun imageSelector(
    @DrawableRes lightId: Int,
    @DrawableRes darkId: Int
): Int {
    return if (isSystemInDarkTheme()) {
        darkId
    } else {
        lightId
    }
}