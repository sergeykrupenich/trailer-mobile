package com.siarheikrupenich.trailermobile.domain.usecase

import com.siarheikrupenich.trailermobile.core.SuspendUseCase
import com.siarheikrupenich.trailermobile.core.network.data.NetworkError
import com.siarheikrupenich.trailermobile.core.network.data.NetworkException
import com.siarheikrupenich.trailermobile.core.network.data.ResultWithFallback
import com.siarheikrupenich.trailermobile.domain.mapper.MovieDtoToDomainMapper
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import java.io.IOException
import javax.inject.Inject

data class GetMovieDetailsParams(
    val movieId: Long,
    val category: MovieCategory = MovieCategory.NOW_PLAYING
)

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: TrailerRepository,
    private val mapper: MovieDtoToDomainMapper
) : SuspendUseCase<GetMovieDetailsParams, ResultWithFallback<TrailerDomainModel>> {

    override suspend fun invoke(param: GetMovieDetailsParams): ResultWithFallback<TrailerDomainModel> {
        return try {
            val movieDto = repository.getMovieDetails(movieId = param.movieId)
            ResultWithFallback.Success(mapper.map(movieDto))
        } catch (e: Exception) {
            val networkError = when (e) {
                is NetworkException -> e.error
                is IOException -> NetworkError.NetworkLost
                else -> NetworkError.Unknown(e)
            }
            ResultWithFallback.Failure(null, networkError)
        }
    }
}
