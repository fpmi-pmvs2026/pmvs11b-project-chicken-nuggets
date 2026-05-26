package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String,

    @SerialName("overview")
    val overview: String = "",

    @SerialName("vote_average")
    val voteAverage: Double = 0.0,

    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),

    @SerialName("poster_path")
    val posterPath: String? = null,

//    @SerialName("original_title")
//    val originalTitle: String? = null,
//    @SerialName("original_language")
//    val originalLanguage: String? = null,
//    @SerialName("release_date")
//    val releaseDate: String? = null,
//    val popularity: Double = 0.0,
//    val voteCount: Int = 0,
//    val adult: Boolean = false,
//    val video: Boolean = false,
//    @SerialName("backdrop_path")
//    val backdropPath: String? = null
)


@Serializable
data class MoviesResponse(
    @SerialName("page")
    val page: Int,

    @SerialName("results")
    val movies: List<Movie>
)