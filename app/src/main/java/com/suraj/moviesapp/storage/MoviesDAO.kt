package com.suraj.moviesapp.storage

import androidx.room.*
import com.suraj.moviesapp.model.MovieItemDTO

@Dao
interface MoviesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieItemDTO)

    @Update
    suspend fun update(movie: MovieItemDTO)

    @Query("SELECT * FROM favorites_table WHERE isFavorite = 1")
    suspend fun fetchFavorites(): List<MovieItemDTO>

    @Query("SELECT * FROM favorites_table WHERE id = :qId ")
    fun fetchMovie(qId: String): MovieItemDTO?
}