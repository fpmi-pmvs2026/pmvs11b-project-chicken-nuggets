package com.example.movies

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.movies.data.MoviesRepository
import com.example.movies.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class MovieWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val movies = getWatchingMovies(context)

        provideContent {
            GlanceTheme {
                WidgetContent(movies)
            }
        }
    }

    @Composable
    private fun WidgetContent(movies: List<MovieWidgetData>) {
        val context = LocalContext.current
        
        if (movies.isEmpty()) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.no_movies_watching),
                    style = TextStyle(color = GlanceTheme.colors.onSurface)
                )
            }
        } else {
            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.surface)
            ) {
                // В Glance используется itemId вместо key
                items(movies, itemId = { it.id.toLong() }) { movie ->
                    MovieItem(movie)
                }
            }
        }
    }

    @Composable
    private fun MovieItem(movie: MovieWidgetData) {
        val context = LocalContext.current
        
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("moviesflow://movie/${movie.id}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(actionStartActivity(intent)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = movie.title,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            
            Spacer(GlanceModifier.height(8.dp))

            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(260.dp) // Крупный размер для 2x3
                    .background(GlanceTheme.colors.secondaryContainer)
            ) {
                if (movie.posterBitmap != null) {
                    Image(
                        provider = ImageProvider(movie.posterBitmap),
                        contentDescription = movie.title,
                        modifier = GlanceModifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", style = TextStyle(color = GlanceTheme.colors.onSecondaryContainer, fontSize = 40.sp))
                    }
                }
                
                // Тег "Смотрю"
                Box(
                    modifier = GlanceModifier
                        .padding(8.dp)
                        .background(GlanceTheme.colors.primary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Смотрю",
                        style = TextStyle(
                            color = GlanceTheme.colors.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            // Отступ снизу для разделения "страниц" при свайпе
            Spacer(GlanceModifier.height(20.dp))
        }
    }

    private suspend fun getWatchingMovies(context: Context): List<MovieWidgetData> = withContext(Dispatchers.IO) {
        val app = context.applicationContext as MoviesApplication
        val settings = app.container.settingsRepository
        val repository = app.container.moviesRepository
        
        // Берем список избранного и фильтруем по тегу
        val favoriteIds = settings.getFavoriteIds()
        val watchingIds = favoriteIds.filter { id ->
            settings.getMovieTag(id) == "Смотрю"
        }

        watchingIds.mapNotNull { id ->
            try {
                val movieId = id.toInt()
                // Сначала ищем в локальной базе, чтобы было мгновенно
                val movie = repository.getLocalMovie(movieId)
                
                if (movie != null) {
                    val bitmap = movie.posterPath?.let { path ->
                        downloadAndResizeBitmap("https://tmdb-proxy-ziqk.onrender.com/image?path=$path")
                    }
                    MovieWidgetData(id = movie.id, title = movie.title, posterBitmap = bitmap)
                } else {
                    // Если в базе нет (редкий случай), тянем из сети
                    val details = repository.getMovieDetails(movieId)
                    val bitmap = details.posterPath?.let { path ->
                        downloadAndResizeBitmap("https://tmdb-proxy-ziqk.onrender.com/image?path=$path")
                    }
                    MovieWidgetData(id = details.id, title = details.title, posterBitmap = bitmap)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun downloadAndResizeBitmap(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection()
            connection.connect()
            val input = connection.getInputStream()
            val original = BitmapFactory.decodeStream(input)
            
            if (original != null) {
                val aspectRatio = original.height.toFloat() / original.width.toFloat()
                val targetWidth = 400
                val targetHeight = (targetWidth * aspectRatio).toInt()
                Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

data class MovieWidgetData(
    val id: Int,
    val title: String,
    val posterBitmap: Bitmap?
)

class MovieWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MovieWidget()
}
