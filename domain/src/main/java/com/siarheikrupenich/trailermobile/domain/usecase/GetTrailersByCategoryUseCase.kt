package com.siarheikrupenich.trailermobile.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.mapper.MovieDtoToDomainMapper
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.paging.TrailerPagingSource
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrailersByCategoryUseCase @Inject constructor(
    private val repository: TrailerRepository,
    private val mapper: MovieDtoToDomainMapper
) : UseCase<MovieCategory, Flow<PagingData<TrailerDomainModel>>> {

    override fun invoke(param: MovieCategory): Flow<PagingData<TrailerDomainModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TrailerPagingSource(repository, mapper, param) }
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
