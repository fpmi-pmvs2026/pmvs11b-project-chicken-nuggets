package com.example.movies

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.movies.ui.ChatScreen
import com.example.movies.ui.FavoritesScreen
import com.example.movies.ui.GenreScreen
import com.example.movies.ui.MovieDetailsScreen
import com.example.movies.ui.MoviesScreen
import com.example.movies.ui.SearchScreen
import com.example.movies.ui.SettingsScreen
import com.example.movies.ui.StartScreen
import com.example.movies.viewModel.GenresViewModel
import com.example.movies.viewModel.MovieDetailsViewModel
import com.example.movies.viewModel.MoviesViewModel
import com.example.movies.viewModel.SettingsViewModel

enum class MovieScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Genres(title = R.string.select_genre),
    Movies(title = R.string.movie_list),
    MovieInfo(title = R.string.movie_info),
    MovieSettings(title = R.string.settings),
    Search(title = R.string.search),
    Favorites(title = R.string.favorites),
    AiChat(title = R.string.ai_chat)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAppBar(
    currentScreen: MovieScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onAiChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentScreen != MovieScreen.Start) {
        TopAppBar(
            title = { Text(stringResource(currentScreen.title), color = Color.White) },
            modifier = modifier,
            actions = {
                IconButton(onClick = onAiChatClick) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.ai_chat),
                        tint = Color.White
                    )
                }
                IconButton(onClick = onFavoritesClick) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.favorites),
                        tint = Color.White
                    )
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        tint = Color.White
                    )
                }
            },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
        )
    }
}

@Composable
fun MovieApp(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: MovieScreen.Start.name
    val baseRoute = route.substringBefore("/")
    val currentScreen =
        MovieScreen.values().firstOrNull { it.name == baseRoute } ?: MovieScreen.Start
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)

    Scaffold(
        topBar = {
            MovieAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onSearchClick = { navController.navigate(MovieScreen.Search.name) },
                onFavoritesClick = { navController.navigate(MovieScreen.Favorites.name) },
                onAiChatClick = { navController.navigate(MovieScreen.AiChat.name) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = MovieScreen.Start.name) {
                StartScreen(
                    onStartButtonClicked = { navController.navigate(MovieScreen.Genres.name) },
                    onSettingsButtonClicked = { navController.navigate(MovieScreen.MovieSettings.name) }
                )
            }
            composable(route = MovieScreen.Genres.name) {
                val genresViewModel: GenresViewModel = viewModel(factory = GenresViewModel.Factory)
                GenreScreen(
                    genresUiState = genresViewModel.genresUiState.collectAsState().value,
                    retryAction = genresViewModel::getGenres,
                    showMovieList = { selectedGenre -> navController.navigate("${MovieScreen.Movies.name}/$selectedGenre") }
                )
            }
            composable(
                route = "${MovieScreen.Movies.name}/{genreId}",
                arguments = listOf(navArgument("genreId") { type = NavType.StringType })
            ) {
                val moviesViewModel: MoviesViewModel = viewModel(factory = MoviesViewModel.Factory)
                MoviesScreen(
                    moviesUiState = moviesViewModel.moviesUiState.collectAsState().value,
                    retryAction = { moviesViewModel.loadMovies() },
                    onLoadMore = { moviesViewModel.loadMovies() },
                    onShowMovieDetails = { selectedMovie -> navController.navigate("${MovieScreen.MovieInfo.name}/$selectedMovie") },
                    settingsViewModel = settingsViewModel
                )
            }
            composable(
                route = "${MovieScreen.MovieInfo.name}/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "moviesflow://movie/{movieId}" })
            ) {
                val movieDetailsViewModel: MovieDetailsViewModel =
                    viewModel(factory = MovieDetailsViewModel.Factory)
                val uiState by movieDetailsViewModel.movieDetailsUiState.collectAsState()
                val movieId = backStackEntry?.arguments?.getString("movieId") ?: ""
                LaunchedEffect(movieId) {
                    if (movieId.isNotEmpty()) {
                        movieDetailsViewModel.getMovieDetails(movieId)
                    }
                }
                MovieDetailsScreen(
                    movieDetailsUiState = uiState,
                    retryAction = {
                        movieDetailsViewModel.getMovieDetails(movieId)
                    },
                    settingsViewModel = settingsViewModel
                )
            }
            composable(route = MovieScreen.MovieSettings.name) {
                SettingsScreen(viewModel = settingsViewModel)
            }
            composable(route = MovieScreen.Search.name) {
                SearchScreen(
                    onShowMovieDetails = { selectedMovie -> navController.navigate("${MovieScreen.MovieInfo.name}/$selectedMovie") },
                    settingsViewModel = settingsViewModel
                )
            }
            composable(route = MovieScreen.Favorites.name) {
                FavoritesScreen(
                    onShowMovieDetails = { selectedMovie -> navController.navigate("${MovieScreen.MovieInfo.name}/$selectedMovie") },
                    settingsViewModel = settingsViewModel
                )
            }
            composable(route = MovieScreen.AiChat.name) {
                ChatScreen(
                    settingsViewModel = settingsViewModel,
                    onShowMovieDetails = { selectedMovie -> navController.navigate("${MovieScreen.MovieInfo.name}/$selectedMovie") }
                )
            }
        }
    }
}
