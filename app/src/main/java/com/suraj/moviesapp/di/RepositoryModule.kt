package com.suraj.moviesapp.di

import com.suraj.moviesapp.storage.MoviesDAO
import com.suraj.moviesapp.network.MovieService
import com.suraj.moviesapp.repository.MoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun provideMovieRepository(movieService: MovieService, moviesDAO: MoviesDAO) = MoviesRepository(movieService, moviesDAO)

}