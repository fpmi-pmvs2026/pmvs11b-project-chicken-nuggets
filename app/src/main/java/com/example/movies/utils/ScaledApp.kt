import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.movies.data.TextScale

@Composable
fun ScaledApp(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val scaleName = prefs.getString("textScale", TextScale.MEDIUM.name) ?: TextScale.MEDIUM.name
    val scale = TextScale.valueOf(scaleName)

    CompositionLocalProvider(
        LocalDensity provides Density(
            LocalDensity.current.density,
            scale.scaleFactor
        )
    ) {
        content()
    }
}
