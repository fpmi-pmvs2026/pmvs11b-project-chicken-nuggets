package com.example.movies.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.http.SslError
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movies.R
import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import com.example.movies.utils.FullscreenWebChromeClient
import com.example.movies.utils.QrCodeGenerator
import com.example.movies.viewModel.MovieDetailsUiState
import com.example.movies.viewModel.MovieDetailsViewModel
import com.example.movies.viewModel.PlayerUiState
import com.example.movies.viewModel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MovieDetailsScreen(
    movieDetailsUiState: MovieDetailsUiState,
    retryAction: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    when (movieDetailsUiState) {
        is MovieDetailsUiState.Loading -> LoadingScreen()
        is MovieDetailsUiState.Error -> ErrorScreen(retryAction = retryAction)
        is MovieDetailsUiState.Success -> MovieInfoScreen(
            movie = movieDetailsUiState.movieDetails,
            settingsViewModel = settingsViewModel
        )
    }
}

@Composable
fun MovieInfoScreen(
    movie: MovieDetails,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    Box {
        Image(
            painter = painterResource(
                imageSelector(
                    R.drawable.movies,
                    R.drawable.movies_dark
                )
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = 28.dp, start = 28.dp, bottom = 14.dp, top = 14.dp)
        ) {
            FirstBlock(movie = movie, settingsViewModel = settingsViewModel)

            Spacer(modifier = Modifier.height(5.dp))

            SecondBlock(
                movie = movie
            )
        }
    }
}

@Composable
fun FirstBlock(
    movie: MovieDetails,
    settingsViewModel: SettingsViewModel
) {
    val favoriteIds by settingsViewModel.favoriteIds.collectAsState()
    val movieTags by settingsViewModel.movieTags.collectAsState()
    val movieRatings by settingsViewModel.movieRatings.collectAsState()

    val isFavorite = favoriteIds.contains(movie.id.toString())
    val currentTag = movieTags[movie.id.toString()]
    val currentRating = movieRatings[movie.id.toString()]

    var showTagDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 30.sp
                )
            }

            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.height(270.dp)
                ) {

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(10.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://tmdb-proxy-ziqk.onrender.com/image?path=${movie.posterPath}")
                                .build(),
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.loading_img),
                            error = painterResource(R.drawable.ic_broken_image),
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White)
                        ) {
                            Text(
                                text = movie.runtime.toString() + " min.",
                                color = Color.Gray,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.White)
                        ) {
                            Text(
                                text = movie.releaseDate,
                                color = Color.Gray,
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                        if (movie.tagline.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color.White)
                            ) {
                                Text(
                                    text = movie.tagline,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f", movie.voteAverage),
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = movie.genres.joinToString(separator = ", ") { it.name },
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                }

                IconButton(onClick = { showShareDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { 
                    settingsViewModel.toggleFavorite(
                        Movie(
                            id = movie.id,
                            title = movie.title,
                            overview = movie.overview,
                            posterPath = movie.posterPath,
                            voteAverage = movie.voteAverage,
                            genreIds = movie.genres.map { it.id }
                        )
                    )
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            if (isFavorite) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Тег
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                            .clickable { showTagDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentTag ?: "Добавить тег",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Оценка
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            .clickable { showRatingDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (currentRating != null) "$currentRating/10" else "Оценить",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    if (showTagDialog) {
        TagSelectionDialog(
            currentTag = currentTag,
            releaseDate = movie.releaseDate,
            onDismiss = { showTagDialog = false },
            onTagSelected = { tag ->
                settingsViewModel.updateMovieTag(movie.id, tag, movie.title, movie.releaseDate)
                showTagDialog = false
            }
        )
    }

    if (showRatingDialog) {
        RatingSelectionDialog(
            currentRating = currentRating,
            onDismiss = { showRatingDialog = false },
            onRatingSelected = { rating ->
                settingsViewModel.updateMovieRating(movie.id, rating)
                showRatingDialog = false
            }
        )
    }

    if (showShareDialog) {
        ShareMovieDialog(
            movie = movie,
            onDismiss = { showShareDialog = false }
        )
    }
}

@Composable
fun ShareMovieDialog(
    movie: MovieDetails,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val deepLink = "moviesflow://movie/${movie.id}"
    
    val qrCodeBitmap = remember(deepLink) {
        QrCodeGenerator.generateQrCode(deepLink)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Поделиться фильмом") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                qrCodeBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp).padding(16.dp)
                    )
                }
                
                Text(
                    text = movie.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = deepLink,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        clipboardManager.setText(AnnotatedString(deepLink))
                        Toast.makeText(context, "Ссылка скопирована", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Копировать")
                    }
                    
                    Button(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Посмотри этот фильм: ${movie.title}\n$deepLink")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Text("Поделиться")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun TagSelectionDialog(
    currentTag: String?,
    releaseDate: String,
    onDismiss: () -> Unit,
    onTagSelected: (String?) -> Unit
) {
    val standardTags = remember(releaseDate) {
        val tags = mutableListOf("Смотрю", "В планах", "Смотрел")
        
        // Проверяем, в будущем ли дата релиза
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val date = sdf.parse(releaseDate)
            if (date != null && date.after(Calendar.getInstance().time)) {
                tags.add("Напомнить")
            }
        } catch (e: Exception) {
            // Игнорируем ошибки парсинга
        }
        tags
    }

    var customTag by remember { mutableStateOf("") }
    var isAddingCustom by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите тег") },
        text = {
            Column {
                standardTags.forEach { tag ->
                    TextButton(
                        onClick = { onTagSelected(tag) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = tag,
                            color = if (currentTag == tag) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                if (isAddingCustom) {
                    TextField(
                        value = customTag,
                        onValueChange = { customTag = it },
                        placeholder = { Text("Свой тег") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { isAddingCustom = false }) { Text("Отмена") }
                        Button(onClick = { if (customTag.isNotBlank()) onTagSelected(customTag) }) { Text("Ок") }
                    }
                } else {
                    TextButton(
                        onClick = { isAddingCustom = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Свой тег...", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                if (currentTag != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { onTagSelected(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Удалить тег", color = Color.Red)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}

@Composable
fun RatingSelectionDialog(
    currentRating: Int?,
    onDismiss: () -> Unit,
    onRatingSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ваша оценка") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                (0..10).forEach { rating ->
                    TextButton(
                        onClick = { onRatingSelected(rating) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$rating/10",
                            fontWeight = if (currentRating == rating) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentRating == rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        }
    )
}

@Composable
fun SecondBlock(
    movie: MovieDetails
) {
    val movieDetailsViewModel: MovieDetailsViewModel =
        viewModel(factory = MovieDetailsViewModel.Factory)

    val playerUiState by movieDetailsViewModel.playerUiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(movie.imdbId) {
        movieDetailsViewModel.loadPlayer(movie.imdbId)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = movie.overview.ifBlank { stringResource(R.string.no_description) },
                color = Color.White,
                modifier = Modifier.padding(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            when (playerUiState) {
                is PlayerUiState.Loading -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is PlayerUiState.Success -> {
                    MoviePlayer(url = (playerUiState as PlayerUiState.Success).url)
                }
                is PlayerUiState.Error -> {
                    Text(
                        text = stringResource(R.string.player_unavailable),
                        color = Color.Red,
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@SuppressLint("ContextCastToActivity", "SetJavaScriptEnabled")
@Composable
fun MoviePlayer(url: String) {
    val activity = LocalContext.current as Activity
    Log.d("MoviePlayer", "Rendering WebView with URL: $url")

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(10.dp)),
        factory = { context ->
            WebView(context).apply {
                // Устанавливаем параметры для WebView
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                WebView.setWebContentsDebuggingEnabled(true)
                setBackgroundColor(android.graphics.Color.BLACK)

                // Включаем аппаратное ускорение
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    allowFileAccess = true
                    allowContentAccess = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    javaScriptCanOpenWindowsAutomatically = true
                }

                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                webChromeClient = object : FullscreenWebChromeClient(activity) {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        Log.d("MoviePlayerJS", "[${consoleMessage?.messageLevel()}] ${consoleMessage?.message()} -- line ${consoleMessage?.lineNumber()}")
                        return true
                    }

                    override fun onPermissionRequest(request: PermissionRequest?) {
                        request?.let {
                            if (it.resources.contains(PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID)) {
                                it.grant(arrayOf(PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID))
                            } else {
                                it.grant(it.resources)
                            }
                        }
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("MoviePlayer", "Finished loading URL: $url")

                        // Запрашиваем данные из localStorage через JS
                        view?.evaluateJavascript(
                            "(function() { return JSON.stringify(localStorage); })();"
                        ) { result ->
                            // В 'result' прилетит JSON-строка со всеми данными localStorage сайта
                            Log.d("MoviePlayerLocalStorage", "Данные плеера: $result")

                            // Твоя задача — распарсить этот JSON и найти ключ вроде "video_progress_XXX" или "player_time"
                        }
                    }

                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        Log.e("MoviePlayer", "Error: $description (Code: $errorCode) for URL: $failingUrl")
                    }

                    @SuppressLint("WebViewClientOnReceivedSslError")
                    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                        Log.e("MoviePlayer", "SSL Error ignored: $error")
                        handler?.proceed()
                    }
                }

                val headers = mutableMapOf<String, String>()
                headers["Referer"] = "https://reyohoho.com"
                loadUrl(url, headers)
            }
        },
        update = { webView ->
            if (url.isNotEmpty() && webView.url != url) {
                Log.d("MoviePlayer", "Updating WebView URL to: $url")
                webView.loadUrl(url)
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun MovieInfoScreenPreview() {
    //MovieInfoScreen(mockMoviesResponse.movies[0])
}
