package com.example.movies.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM favorite_movies")
    fun getAllFavorites(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM favorite_movies WHERE tag = :tag")
    suspend fun getMoviesByTag(tag: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("DELETE FROM favorite_movies WHERE id = :id")
    suspend fun deleteMovieById(id: Int)

    @Query("SELECT * FROM favorite_movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("UPDATE favorite_movies SET tag = :tag WHERE id = :id")
    suspend fun updateTag(id: Int, tag: String?)
}
