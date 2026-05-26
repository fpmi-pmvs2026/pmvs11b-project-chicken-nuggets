package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String,

    @SerialName("overview")
    val overview: String,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("backdrop_path")
    val backdropPath: String?,

    @SerialName("vote_average")
    val voteAverage: Double,

    @SerialName("genres")
    val genres: List<Genre>,

    @SerialName("release_date")
    val releaseDate: String,

    @SerialName("runtime")
    val runtime: Int,

    @SerialName("tagline")
    val tagline: String,

    @SerialName("imdb_id")
    val imdbId: String
)