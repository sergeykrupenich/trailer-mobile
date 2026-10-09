package com.siarheikrupenich.trailermobile.presentation.usecase

import com.siarheikrupenich.trailermobile.core.SuspendUseCase
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.usecase.ToggleFavoriteUseCase
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import javax.inject.Inject

class ToggleFavoriteUiUseCase @Inject constructor(
    private val toggleFavorite: ToggleFavoriteUseCase
) : SuspendUseCase<TrailerUi, Unit> {

    override suspend fun invoke(param: TrailerUi) {
        toggleFavorite(
            param = TrailerDomainModel(
                id = param.id,
                title = param.title,
                overview = param.description,
                posterPath = param.posterPath,
                backdropPath = param.backdropPath,
                voteAverage = param.voteAverage,
                releaseDate = param.releaseDate
            )
        )
    }
}
