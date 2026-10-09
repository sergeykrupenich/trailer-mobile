package com.siarheikrupenich.trailermobile.data.di

import android.content.Context
import androidx.room.Room
import com.siarheikrupenich.trailermobile.data.api.TrailerApi
import com.siarheikrupenich.trailermobile.data.db.TrailerDatabase
import com.siarheikrupenich.trailermobile.data.db.dao.MovieDao
import com.siarheikrupenich.trailermobile.data.repository.TrailerRepositoryImpl
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindTrailerRepository(
        impl: TrailerRepositoryImpl
    ): TrailerRepository

    companion object {
        @Provides
        @Singleton
        fun provideTrailerApi(retrofit: Retrofit): TrailerApi {
            return retrofit.create(TrailerApi::class.java)
        }

        @Provides
        @Singleton
        fun provideTrailerDatabase(
            @ApplicationContext context: Context
        ): TrailerDatabase {
            return Room.databaseBuilder(
                context,
                TrailerDatabase::class.java,
                TRAILER_DB
            ).fallbackToDestructiveMigration(true).build()
        }

        @Provides
        @Singleton
        fun provideMovieDao(database: TrailerDatabase): MovieDao {
            return database.movieDao()
        }

        private const val TRAILER_DB = "trailer_database"
    }
}
