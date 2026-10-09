package com.siarheikrupenich.trailermobile.presentation.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.usecase.GetFavoriteMoviesUseCase
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.mapper.TrailerDomainToUiMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteMoviesUiUseCase @Inject constructor(
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
    private val mapper: TrailerDomainToUiMapper
) : UseCase<Unit, Flow<List<TrailerUi>>> {

    override fun invoke(param: Unit): Flow<List<TrailerUi>> {
        return getFavoriteMovies(Unit).map { domainList ->
            domainList.map { mapper.map(it) }
        }
    }
}
