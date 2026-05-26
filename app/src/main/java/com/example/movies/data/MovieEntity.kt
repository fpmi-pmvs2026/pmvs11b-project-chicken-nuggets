package com.example.movies.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.movies.model.Genre
import com.example.movies.model.Movie
import com.example.movies.model.MovieDetails
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "favorite_movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val voteAverage: Double,
    val posterPath: String?,
    val backdropPath: String? = null,
    val releaseDate: String = "",
    val runtime: Int = 0,
    val tagline: String = "",
    val imdbId: String = "",
    val genresJson: String = "[]",
    val tag: String? = null // Добавляем тег в сущность БД
)

class GenreConverter {
    @TypeConverter
    fun fromGenreList(value: List<Genre>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

fun MovieEntity.asDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        voteAverage = voteAverage,
        posterPath = posterPath
    )
}

fun MovieEntity.asDetailsModel(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        genres = GenreConverter().toGenreList(genresJson),
        releaseDate = releaseDate,
        runtime = runtime,
        tagline = tagline,
        imdbId = imdbId
    )
}

fun MovieDetails.asEntity(tag: String? = null): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        voteAverage = voteAverage,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        runtime = runtime,
        tagline = tagline,
        imdbId = imdbId,
        genresJson = Json.encodeToString(genres),
        tag = tag
    )
}

fun Movie.asEntity(tag: String? = null): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        voteAverage = voteAverage,
        posterPath = posterPath,
        tag = tag
    )
}
