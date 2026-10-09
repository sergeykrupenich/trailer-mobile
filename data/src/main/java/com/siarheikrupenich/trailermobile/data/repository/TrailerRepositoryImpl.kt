package com.siarheikrupenich.trailermobile.data.repository

import com.siarheikrupenich.trailermobile.data.api.TrailerApi
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto
import javax.inject.Inject

class TrailerRepositoryImpl @Inject constructor(
    private val trailerApi: TrailerApi
) : TrailerRepository {

    override suspend fun getMovies(category: String, page: Int): NowPlayingResponseDto {
        return trailerApi.getMovies(category = category, page = page)
    }

    override suspend fun getMovieDetails(movieId: Long): MovieDto {
        return trailerApi.getMovieDetails(movieId = movieId)
    }
}
