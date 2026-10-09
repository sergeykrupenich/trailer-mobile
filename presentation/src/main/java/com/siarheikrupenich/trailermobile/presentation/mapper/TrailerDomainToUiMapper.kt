package com.siarheikrupenich.trailermobile.presentation.mapper

import com.siarheikrupenich.trailermobile.core.Mapper
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import javax.inject.Inject

class TrailerDomainToUiMapper @Inject constructor() : Mapper<TrailerDomainModel, TrailerUi> {

    override fun map(param: TrailerDomainModel): TrailerUi {
        return TrailerUi(
            id = param.id,
            title = param.title,
            description = param.overview,
            posterPath = param.posterPath,
            backdropPath = param.backdropPath,
            voteAverage = param.voteAverage,
            releaseDate = param.releaseDate
        )
    }
}
