package com.suraj.moviesapp.di

import android.app.Application
import com.suraj.moviesapp.storage.ApplicationDatabase
import com.suraj.moviesapp.storage.MoviesDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideApplicationDatabase(app: Application): ApplicationDatabase {
        return ApplicationDatabase.getInstance(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideMoviesDAO(appDb: ApplicationDatabase): MoviesDAO {
        return appDb.moviesDAO()
    }
}