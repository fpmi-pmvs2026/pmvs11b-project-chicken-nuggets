package com.example.movies.data

import com.example.movies.model.Genre
import com.example.movies.model.Movie

data class GenresResponse(val genres: List<Genre>)

data class MoviesResponse(
    val page: Int,
    val movies: List<Movie>
)

val mockMoviesResponse = MoviesResponse (
    page = 1,
    movies = listOf(
        Movie(
            id = 1311031,
            title = "Demon Slayer: Kimetsu no Yaiba Infinity Castle",
            overview = "The Demon Slayer Corps are drawn into the Infinity Castle...",
            voteAverage = 7.801,
            genreIds = listOf(16, 28, 14, 53),
            posterPath = "/sUsVimPdA1l162FvdBIlmKBlWHx.jpg",
        )
    )
)
/*
val mockGenresResponse = GenresResponse(
    genres = listOf(
        Genre(28", "Action"),
        Genre(12", "Adventure"),
        Genre(16", "Animation"),
        Genre(35", "Comedy"),
        Genre(80", "Crime"),
        Genre(99", "Documentary"),
        Genre(18", "Drama"),
        Genre(10751", "Family"),
        Genre(14", "Fantasy"),
        Genre(36", "History"),
        Genre(27", "Horror"),
        Genre(10402", "Music"),
        Genre(9648", "Mystery"),
        Genre(10749", "Romance"),
        Genre(878, "Science Fiction"),
        Genre(10770", "TV Movie"),
        Genre(53, "Thriller"),
        Genre(10752", "War"),
        Genre(37, "Western")
    )
)*/
