package com.siarheikrupenich.trailermobile.domain.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.mapper.MovieDtoToDomainMapper
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteMoviesUseCase @Inject constructor(
    private val repository: TrailerRepository,
    private val mapper: MovieDtoToDomainMapper
) : UseCase<Unit, Flow<List<TrailerDomainModel>>> {

    override fun invoke(param: Unit): Flow<List<TrailerDomainModel>> {
        return repository.getFavoriteMovies().map { dtoList ->
            dtoList.map { dto -> mapper.map(dto) }
        }
    }
}
