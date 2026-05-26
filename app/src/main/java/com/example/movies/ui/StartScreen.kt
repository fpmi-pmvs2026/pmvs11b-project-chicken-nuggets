package com.example.movies.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movies.R
import com.example.movies.ui.theme.MoviesTheme

@Composable
fun StartScreen(
    onStartButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(
                imageSelector(
                    R.drawable.main,
                    R.drawable.main_dark
                )
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = modifier.fillMaxSize()
        ) {
            // Start button
            Button(
                onClick = { onStartButtonClicked() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(130.dp)
                    .width(270.dp)
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.start),
                    fontSize = 25.sp
                )
            }

            // Settings button
            Button(
                onClick = { onSettingsButtonClicked() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .height(130.dp)
                    .width(270.dp)
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun StartScreenPreview() {
    MoviesTheme {
        StartScreen({}, {})
    }
}