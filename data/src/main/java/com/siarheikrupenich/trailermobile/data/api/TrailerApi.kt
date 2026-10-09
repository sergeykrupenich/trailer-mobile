package com.siarheikrupenich.trailermobile.data.api

import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TrailerApi {

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): NowPlayingResponseDto
}
