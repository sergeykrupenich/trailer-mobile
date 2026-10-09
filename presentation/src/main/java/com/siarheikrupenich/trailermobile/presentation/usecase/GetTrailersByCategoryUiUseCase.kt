package com.siarheikrupenich.trailermobile.presentation.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.domain.usecase.GetTrailersByCategoryUseCase
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.mapper.TrailerDomainToUiMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTrailersByCategoryUiUseCase @Inject constructor(
    private val getTrailersByCategoryUseCase: GetTrailersByCategoryUseCase,
    private val mapper: TrailerDomainToUiMapper
) {
    operator fun invoke(category: MovieCategory): Flow<PagingData<TrailerUi>> {
        return getTrailersByCategoryUseCase(category).map { pagingData ->
            pagingData.map { domainModel -> mapper.map(domainModel) }
        }
    }
}
