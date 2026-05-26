package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movies.R
import com.example.movies.model.Movie
import com.example.movies.viewModel.FavoritesViewModel
import com.example.movies.viewModel.MoviesUiState
import com.example.movies.viewModel.SettingsViewModel

@Composable
fun FavoritesScreen(
    onShowMovieDetails: (Int) -> Unit,
    settingsViewModel: SettingsViewModel,
    favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)
) {
    val favoritesUiState by favoritesViewModel.favoritesUiState.collectAsState()

    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites()
    }

    when (val uiState = favoritesUiState) {
        is MoviesUiState.Loading -> LoadingScreen()
        is MoviesUiState.Error -> ErrorScreen(retryAction = favoritesViewModel::loadFavorites)
        is MoviesUiState.Success -> {
            FavoritesListScreen(
                movies = uiState.movies,
                onShowMovieDetails = onShowMovieDetails,
                settingsViewModel = settingsViewModel
            )
        }
    }
}

@Composable
fun FavoritesListScreen(
    movies: List<Movie>,
    onShowMovieDetails: (Int) -> Unit,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                imageSelector(
                    R.drawable.movies,
                    R.drawable.movies_dark
                )
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        LazyColumn(modifier = modifier) {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = onShowMovieDetails,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
