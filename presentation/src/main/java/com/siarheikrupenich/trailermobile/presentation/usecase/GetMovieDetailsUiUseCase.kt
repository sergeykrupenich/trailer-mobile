package com.siarheikrupenich.trailermobile.presentation.usecase

import com.siarheikrupenich.trailermobile.core.SuspendUseCase
import com.siarheikrupenich.trailermobile.core.network.data.ResultWithFallback
import com.siarheikrupenich.trailermobile.core.network.data.map
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.domain.usecase.GetMovieDetailsParams
import com.siarheikrupenich.trailermobile.domain.usecase.GetMovieDetailsUseCase
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.mapper.TrailerDomainToUiMapper
import javax.inject.Inject

data class GetMovieDetailsUiParams(
    val movieId: Long,
    val category: MovieCategory = MovieCategory.NOW_PLAYING
)

class GetMovieDetailsUiUseCase @Inject constructor(
    private val getMovieDetails: GetMovieDetailsUseCase,
    private val mapper: TrailerDomainToUiMapper
) : SuspendUseCase<GetMovieDetailsUiParams, ResultWithFallback<TrailerUi>> {

    override suspend fun invoke(param: GetMovieDetailsUiParams): ResultWithFallback<TrailerUi> {
        return getMovieDetails(GetMovieDetailsParams(param.movieId, param.category)).map { domainModel ->
            mapper.map(domainModel)
        }
    }
}
