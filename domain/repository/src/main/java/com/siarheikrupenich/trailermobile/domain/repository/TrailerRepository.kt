package com.siarheikrupenich.trailermobile.domain.repository

import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto

interface TrailerRepository {
    suspend fun getMovies(category: String, page: Int): NowPlayingResponseDto
}
