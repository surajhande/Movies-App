package com.suraj.moviesapp.repository

import android.util.Log
import com.suraj.moviesapp.model.MovieDetailsDTO
import com.suraj.moviesapp.storage.MoviesDAO
import com.suraj.moviesapp.model.MovieItemDTO
import com.suraj.moviesapp.model.MovieItemResponse
import com.suraj.moviesapp.network.MovieService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val service: MovieService,
    private val moviesDAO: MoviesDAO
) : Repository {

    fun fetchMovies(query: String, page: Int): Flow<MovieItemResponse> = flow {
        try {
            val response = service.search(query, page)
            CoroutineScope(Dispatchers.IO).launch {
                response.search.forEach {
                    val movieInDb = moviesDAO.fetchMovie(it.id)
                    if (movieInDb != null)
                        it.isFavorite = movieInDb.isFavorite
                }
            }
            emit(response)
        } catch (e: Exception) {
            Log.d(
                "MoviesRepository",
                "Something went wrong searching for $query ${e.printStackTrace()}"
            )
            emit(MovieItemResponse(arrayListOf(), 0, "false"))
        }
    }

    fun fetchMovie(id: String): Deferred<MovieItemDTO?> =
        CoroutineScope(Dispatchers.IO).async { moviesDAO.fetchMovie(id) }

    fun insertFavorite(movie: MovieItemDTO) = CoroutineScope(Dispatchers.IO).launch {
        moviesDAO.insert(movie)
    }

    fun removeFavorite(movie: MovieItemDTO) = CoroutineScope(Dispatchers.IO).launch {
        moviesDAO.update(movie)
    }

    fun fetchFavorites(): Flow<List<MovieItemDTO>> = flow {
        try {
            val result = moviesDAO.fetchFavorites()
            emit(result)
        } catch (e: Exception) {
            Log.d(
                "MoviesRepository",
                "Something went wrong fetching from local db. ${e.printStackTrace()}"
            )
            emit(emptyList())
        }
    }

    fun fetchMovieDetails(id: String): Flow<MovieDetailsDTO> = flow {
        try {
            val result = service.getDetails(id)
            emit(result)
        } catch (e: Exception) {
            Log.d(
                "MovieRepository",
                "Something went wrong fetching movie details. ${e.printStackTrace()}"
            )
            emit(MovieDetailsDTO("", "", "", "", "", "", "", ""))
        }
    }
}