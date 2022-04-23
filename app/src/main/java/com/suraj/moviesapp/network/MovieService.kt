package com.suraj.moviesapp.network

import com.suraj.moviesapp.model.MovieDetailsDTO
import com.suraj.moviesapp.model.MovieItemResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    companion object {
        const val API_KEY = "42b11656"
    }

    @GET("/")
    suspend fun search(
        @Query("s") s: String,
        @Query("page") page: Int,
        @Query("apikey") apiKey: String = API_KEY
    ): MovieItemResponse

    @GET("/")
    suspend fun getDetails(
        @Query("i") id: String,
        @Query("apikey") apiKey: String = API_KEY
    ): MovieDetailsDTO

}