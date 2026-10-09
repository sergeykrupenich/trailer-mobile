package com.siarheikrupenich.trailermobile.domain.usecase

import com.siarheikrupenich.trailermobile.core.SuspendUseCase
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: TrailerRepository
) : SuspendUseCase<TrailerDomainModel, Unit> {

    override suspend fun invoke(param: TrailerDomainModel) {
        repository.toggleFavorite(
            movie = MovieDto(
                id = param.id,
                title = param.title,
                overview = param.overview,
                posterPath = param.posterPath,
                backdropPath = param.backdropPath,
                voteAverage = param.voteAverage,
                releaseDate = param.releaseDate
            )
        )
    }
}
