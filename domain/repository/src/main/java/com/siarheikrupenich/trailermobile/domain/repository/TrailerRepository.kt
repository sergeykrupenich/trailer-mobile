package com.siarheikrupenich.trailermobile.domain.repository

import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto
import kotlinx.coroutines.flow.Flow

interface TrailerRepository {
    suspend fun getMovies(category: String, page: Int): NowPlayingResponseDto
    suspend fun getMovieDetails(movieId: Long): MovieDto
    fun getFavoriteMovies(): Flow<List<MovieDto>>
    fun isFavorite(movieId: Long): Flow<Boolean>
    suspend fun toggleFavorite(movie: MovieDto)
    fun getOfflineStatus(): Flow<Boolean>
}
