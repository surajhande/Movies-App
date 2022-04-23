package com.suraj.moviesapp.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.suraj.moviesapp.model.MovieItemDTO
import javax.inject.Singleton

@Singleton
@Database(entities = [MovieItemDTO::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun moviesDAO(): MoviesDAO

    companion object {
        private var instance: ApplicationDatabase? = null

        fun getInstance(context: Context): ApplicationDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ApplicationDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ApplicationDatabase::class.java,
                "application_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}