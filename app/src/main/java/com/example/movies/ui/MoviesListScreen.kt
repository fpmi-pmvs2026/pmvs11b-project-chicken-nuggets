package com.example.movies.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movies.R
import com.example.movies.data.mockMoviesResponse
import com.example.movies.model.Movie
import com.example.movies.viewModel.MoviesUiState
import com.example.movies.viewModel.SettingsViewModel

@Composable
fun MoviesScreen(
    moviesUiState: MoviesUiState,
    retryAction: () -> Unit,
    onLoadMore: () -> Unit,
    onShowMovieDetails: (Int) -> Unit,
    settingsViewModel: SettingsViewModel
) {
    when (moviesUiState) {
        is MoviesUiState.Loading -> LoadingScreen()
        is MoviesUiState.Error -> ErrorScreen(retryAction)
        is MoviesUiState.Success -> MoviesListScreen(
            movies = moviesUiState.movies,
            onLoadMore = onLoadMore,
            onShowMovieDetails = onShowMovieDetails,
            settingsViewModel = settingsViewModel
        )
    }
}

@Composable
fun MoviesListScreen(
    movies: List<Movie>,
    onLoadMore: () -> Unit,
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
            itemsIndexed(movies) { index, movie ->
                MovieCard(
                    movie = movie,
                    onClick = onShowMovieDetails,
                    settingsViewModel = settingsViewModel
                )

                if (index == movies.lastIndex) {
                    onLoadMore()
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    onClick: (Int) -> Unit,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val posterUrl = "https://tmdb-proxy-ziqk.onrender.com/image?path=${movie.posterPath}"
    val favoriteIds by settingsViewModel.favoriteIds.collectAsState()
    val movieTags by settingsViewModel.movieTags.collectAsState()
    val movieRatings by settingsViewModel.movieRatings.collectAsState()
    
    val isFavorite = favoriteIds.contains(movie.id.toString())
    val tag = movieTags[movie.id.toString()]
    val userRating = movieRatings[movie.id.toString()]

    Card(
        modifier = modifier
            .padding(start = 28.dp, end = 28.dp, bottom = 12.dp)
            .fillMaxWidth()
            .height(180.dp) // Increased height slightly to accommodate tags
            .clickable { onClick(movie.id) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            if (isSystemInDarkTheme()) Color.Black
            else Color.White
        )
    ) {
        Row(modifier = modifier.padding(8.dp)) {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(30.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.3f)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
                    error = painterResource(R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = movie.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (isFavorite && userRating != null) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$userRating/10",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    
                    if (isFavorite && tag != null) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = movie.overview,
                        maxLines = if (tag != null) 2 else 3,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingStars(movie.voteAverage)

                    IconButton(onClick = { settingsViewModel.toggleFavorite(movie) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingStars(rating: Double) {
    Row {
        repeat(10) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
