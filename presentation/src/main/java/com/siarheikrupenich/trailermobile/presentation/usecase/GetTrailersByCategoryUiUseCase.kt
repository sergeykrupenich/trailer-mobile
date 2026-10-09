package com.siarheikrupenich.trailermobile.presentation.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.domain.usecase.GetTrailersByCategoryUseCase
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.mapper.TrailerDomainToUiMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTrailersByCategoryUiUseCase @Inject constructor(
    private val getTrailersByCategory: GetTrailersByCategoryUseCase,
    private val mapper: TrailerDomainToUiMapper
) : UseCase<MovieCategory, Flow<PagingData<TrailerUi>>> {

    override fun invoke(param: MovieCategory): Flow<PagingData<TrailerUi>> {
        return getTrailersByCategory(param).map { pagingData ->
            pagingData.map { domainModel -> mapper.map(domainModel) }
        }
    }
}
