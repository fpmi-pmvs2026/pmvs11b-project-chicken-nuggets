package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movies.R
import com.example.movies.model.GenreDTO
import com.example.movies.ui.theme.MoviesTheme
import com.example.movies.viewModel.GenresUiState


@Composable
fun GenreScreen(
    genresUiState: GenresUiState,
    retryAction: () -> Unit,
    showMovieList: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (genresUiState) {
        is GenresUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is GenresUiState.Success -> GenresGridScreen(
            genres = genresUiState.genres,
            showMovieList = showMovieList
        )

        is GenresUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun GenresGridScreen(
    genres: List<GenreDTO>,
    showMovieList: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(imageSelector(
                R.drawable.genres,
                R.drawable.genres_dark)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 28.dp,
                end = 28.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 5.dp)
        ) {
            items(genres) { genre ->
                GenreBox(genre, showMovieList = showMovieList)
            }
        }
    }
}

@Composable
fun GenreBox(
    genre: GenreDTO,
    showMovieList: (Int) -> Unit
) {
    val resourceName = genre.imageName

    val context = LocalContext.current
    val imageResId = remember(resourceName) {
        val resolvedId =
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resolvedId != 0) resolvedId else R.drawable.ic_broken_image
    }

    Button(
        onClick = { showMovieList(genre.id) },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.size(160.dp, 200.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.5f),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = genre.name,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GenreScreenPreview() {
    MoviesTheme {
        //GenreScreen(genres = mockGenresResponse)
    }
}

@Preview
@Composable
fun GenreBoxPreview() {
    GenreBox(GenreDTO(28, "Action", imageName = "")) {}
}
