package com.example.movies.ui

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movies.R
import com.example.movies.data.TextScale
import com.example.movies.data.Theme
import com.example.movies.data.supportedLanguages
import com.example.movies.viewModel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedScale by viewModel.selectedScale.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                imageSelector(
                    R.drawable.movies,
                    R.drawable.movies_dark
                )
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier.padding(top = 5.dp)
        ) {
            item {
                LanguageOption(selectedLanguage) { code ->
                    viewModel.selectLanguage(code)

                    val activity = context as? Activity
                    activity?.recreate()
                }
            }
            item {
                TextScaleOption(selectedScale) { scale ->
                    viewModel.selectTextScale(scale)

                    val activity = context as? Activity
                    activity?.recreate()
                }
            }
            item {
                ThemeOption(selectedTheme) { theme ->
                    viewModel.selectTheme(theme)
                }
            }
        }
    }
}


@Composable
fun LanguageOption(
    selectedLanguageCode: String,
    onSelectedLanguage: (String) -> Unit
) {
    SettingCard(title = stringResource(R.string.setting_language)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            supportedLanguages.forEach { language ->
                val isSelected = language.languageCode == selectedLanguageCode

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectedLanguage(language.languageCode) }
                    )

                    // TODO: language flag
                    //Image()

                    Text(
                        text = language.language,
                        modifier = Modifier.padding(start = 12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TextScaleOption(
    selectedScale: TextScale,
    onSelectScale: (TextScale) -> Unit
) {
    SettingCard(title = stringResource(R.string.setting_text_scale)) {
//        TODO: text scale setting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextScale.values().forEach { scale ->
                val isSelected = scale == selectedScale

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectScale(scale) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(scale.labelResId),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                    )
                }

            }
        }
    }
}

@Composable
fun ThemeOption(
    selectedTheme: Theme,
    onSelectTheme: (Theme) -> Unit
) {
    SettingCard(title = "Theme") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Theme.values().forEach { theme ->
                val isSelected = theme == selectedTheme

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectTheme(theme) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (theme) {
                            Theme.LIGHT -> "Light"
                            Theme.DARK -> "Dark"
                        },
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingCard(
    title: String = stringResource(R.string.setting_language),
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(start = 28.dp, end = 28.dp, bottom = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(start = 25.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(Color.Black)
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
fun LanguageOptionPreview() {
    LanguageOption("en") { }
}

@Preview
@Composable
fun TextScaleOptionPreview() {
    TextScaleOption(TextScale.BIG) {}
}

@Preview
@Composable
fun ThemeOptionPreview() {
    ThemeOption(Theme.LIGHT) {}
}

//@Preview(showSystemUi = true)
//@Composable
//fun SettingsScreenPreview() {
//    SettingsScreen()
//}
