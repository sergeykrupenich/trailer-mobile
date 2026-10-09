package com.siarheikrupenich.trailermobile.domain.mapper

import com.siarheikrupenich.trailermobile.core.Mapper
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import javax.inject.Inject

class MovieDtoToDomainMapper @Inject constructor() : Mapper<MovieDto, TrailerDomainModel> {

    override fun map(param: MovieDto): TrailerDomainModel {
        val fullPosterPath = param.posterPath?.let { "$IMAGE_TMDB_500$it" }
        val fullBackdropPath = param.backdropPath?.let { "$IMAGE_TMDB_780$it" }

        return TrailerDomainModel(
            id = param.id,
            title = param.title.orEmpty(),
            overview = param.overview.orEmpty(),
            posterPath = fullPosterPath,
            backdropPath = fullBackdropPath,
            voteAverage = param.voteAverage ?: 0.0,
            releaseDate = param.releaseDate.orEmpty()
        )
    }

    companion object {
        private const val IMAGE_TMDB_500 = "https://image.tmdb.org/t/p/w500"
        private const val IMAGE_TMDB_780 = "https://image.tmdb.org/t/p/w780"
    }
}
