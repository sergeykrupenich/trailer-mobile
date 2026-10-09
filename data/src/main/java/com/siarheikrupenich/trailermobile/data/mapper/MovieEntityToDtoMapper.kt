package com.siarheikrupenich.trailermobile.data.mapper

import com.siarheikrupenich.trailermobile.core.Mapper
import com.siarheikrupenich.trailermobile.data.db.entity.MovieEntity
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import javax.inject.Inject

class MovieEntityToDtoMapper @Inject constructor() : Mapper<MovieEntity, MovieDto> {

    override fun map(param: MovieEntity): MovieDto {
        return MovieDto(
            id = param.id,
            title = param.title,
            overview = param.overview,
            posterPath = param.posterPath,
            backdropPath = param.backdropPath,
            voteAverage = param.voteAverage,
            releaseDate = param.releaseDate
        )
    }
}
