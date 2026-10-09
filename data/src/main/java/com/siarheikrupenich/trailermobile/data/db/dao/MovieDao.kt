package com.siarheikrupenich.trailermobile.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.siarheikrupenich.trailermobile.data.db.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE isFavorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM movies WHERE id = :movieId AND isFavorite = 1)")
    fun isFavorite(movieId: Long): Flow<Boolean>

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    suspend fun getMovieById(movieId: Long): MovieEntity?

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY id ASC")
    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY id ASC")
    suspend fun getMoviesByCategorySync(category: String): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("""
        UPDATE movies 
        SET title = :title, 
            overview = :overview, 
            posterPath = :posterPath, 
            backdropPath = :backdropPath, 
            voteAverage = :voteAverage, 
            releaseDate = :releaseDate, 
            category = :category, 
            lastUpdated = :lastUpdated 
        WHERE id = :id
    """)
    suspend fun updateMovieMetadata(
        id: Long,
        title: String,
        overview: String,
        posterPath: String?,
        backdropPath: String?,
        voteAverage: Double,
        releaseDate: String,
        category: String,
        lastUpdated: Long
    )

    @Transaction
    suspend fun insertMoviesPreservingFavorites(movies: List<MovieEntity>) {
        for (movie in movies) {
            val existing = getMovieById(movie.id)
            if (existing != null) {
                updateMovieMetadata(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    posterPath = movie.posterPath,
                    backdropPath = movie.backdropPath,
                    voteAverage = movie.voteAverage,
                    releaseDate = movie.releaseDate,
                    category = movie.category,
                    lastUpdated = movie.lastUpdated
                )
            } else {
                insertMovie(movie)
            }
        }
    }

    @Query("UPDATE movies SET isFavorite = :isFavorite WHERE id = :movieId")
    suspend fun updateFavoriteState(movieId: Long, isFavorite: Boolean)

    @Transaction
    suspend fun toggleFavorite(movie: MovieEntity) {
        val existing = getMovieById(movie.id)
        if (existing != null) {
            updateFavoriteState(movie.id, !existing.isFavorite)
        } else {
            insertMovie(movie.copy(isFavorite = true))
        }
    }
}
