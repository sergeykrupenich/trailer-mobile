package com.siarheikrupenich.trailermobile.data.mapper

import com.siarheikrupenich.trailermobile.core.Mapper
import com.siarheikrupenich.trailermobile.data.db.entity.MovieEntity
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import javax.inject.Inject

data class MovieDtoToEntityParams(
    val dto: MovieDto,
    val category: String = "",
    val isFavorite: Boolean = false
)

class MovieDtoToEntityMapper @Inject constructor() : Mapper<MovieDtoToEntityParams, MovieEntity> {

    override fun map(param: MovieDtoToEntityParams): MovieEntity {
        val dto = param.dto
        return MovieEntity(
            id = dto.id,
            title = dto.title.orEmpty(),
            overview = dto.overview.orEmpty(),
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            voteAverage = dto.voteAverage ?: 0.0,
            releaseDate = dto.releaseDate.orEmpty(),
            category = param.category,
            isFavorite = param.isFavorite
        )
    }
}
