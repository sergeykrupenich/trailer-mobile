package com.siarheikrupenich.trailermobile.domain.repository

import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto

interface TrailerRepository {
    suspend fun getNowPlayingMovies(page: Int): NowPlayingResponseDto
}
