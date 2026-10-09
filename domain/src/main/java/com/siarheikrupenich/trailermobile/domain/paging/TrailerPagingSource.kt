package com.siarheikrupenich.trailermobile.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.siarheikrupenich.trailermobile.domain.mapper.MovieDtoToDomainMapper
import com.siarheikrupenich.trailermobile.domain.model.TrailerDomainModel
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository

class TrailerPagingSource(
    private val repository: TrailerRepository,
    private val mapper: MovieDtoToDomainMapper
) : PagingSource<Int, TrailerDomainModel>() {

    override fun getRefreshKey(state: PagingState<Int, TrailerDomainModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrailerDomainModel> {
        val page = params.key ?: 1
        return try {
            val response = repository.getNowPlayingMovies(page = page)
            val domainModels = response.results.map { mapper.map(it) }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (page >= response.totalPages || domainModels.isEmpty()) null else page + 1

            LoadResult.Page(
                data = domainModels,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
