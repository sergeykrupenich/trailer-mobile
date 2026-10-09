package com.siarheikrupenich.trailermobile.data.repository

import com.siarheikrupenich.trailermobile.data.api.TrailerApi
import com.siarheikrupenich.trailermobile.data.db.dao.MovieDao
import com.siarheikrupenich.trailermobile.data.mapper.MovieDtoToEntityMapper
import com.siarheikrupenich.trailermobile.data.mapper.MovieDtoToEntityParams
import com.siarheikrupenich.trailermobile.data.mapper.MovieEntityToDtoMapper
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrailerRepositoryImpl @Inject constructor(
    private val trailerApi: TrailerApi,
    private val movieDao: MovieDao,
    private val dtoToEntityMapper: MovieDtoToEntityMapper,
    private val entityToDtoMapper: MovieEntityToDtoMapper
) : TrailerRepository {

    override suspend fun getMovies(category: String, page: Int): NowPlayingResponseDto {
        return try {
            val response = trailerApi.getMovies(category = category, page = page)
            val pageSize = response.results.size
            val entities = response.results.mapIndexed { index, dto ->
                val orderIndex = (page - 1) * pageSize + index
                dtoToEntityMapper.map(
                    MovieDtoToEntityParams(
                        dto = dto,
                        category = category,
                        orderIndex = orderIndex
                    )
                )
            }
            movieDao.insertMoviesPreservingFavorites(entities)
            response
        } catch (e: Exception) {
            val cachedEntities = movieDao.getMoviesByCategorySync(category)
            if (cachedEntities.isNotEmpty()) {
                NowPlayingResponseDto(
                    page = 1,
                    results = cachedEntities.map { entityToDtoMapper.map(it) },
                    totalPages = 1,
                    totalResults = cachedEntities.size
                )
            } else {
                throw e
            }
        }
    }

    override suspend fun getMovieDetails(movieId: Long): MovieDto {
        return try {
            val movieDto = trailerApi.getMovieDetails(movieId = movieId)
            val cached = movieDao.getMovieById(movieId)
            val category = cached?.category.orEmpty()
            val orderIndex = cached?.orderIndex ?: 0
            val isFavorite = cached?.isFavorite ?: false

            val entity = dtoToEntityMapper.map(
                MovieDtoToEntityParams(
                    dto = movieDto,
                    category = category,
                    isFavorite = isFavorite,
                    orderIndex = orderIndex
                )
            )
            movieDao.insertMovie(entity)
            movieDto
        } catch (e: Exception) {
            val cached = movieDao.getMovieById(movieId)
            cached?.let { entityToDtoMapper.map(it) } ?: throw e
        }
    }

    override fun getFavoriteMovies(): Flow<List<MovieDto>> {
        return movieDao.getFavoriteMovies().map { entities ->
            entities.map { entityToDtoMapper.map(it) }
        }
    }

    override fun isFavorite(movieId: Long): Flow<Boolean> {
        return movieDao.isFavorite(movieId)
    }

    override suspend fun toggleFavorite(movie: MovieDto) {
        val cached = movieDao.getMovieById(movie.id)
        val isFavoriteNow = cached?.isFavorite ?: false
        val category = cached?.category.orEmpty()
        val orderIndex = cached?.orderIndex ?: 0
        val entity = dtoToEntityMapper.map(
            MovieDtoToEntityParams(
                dto = movie,
                category = category,
                isFavorite = !isFavoriteNow,
                orderIndex = orderIndex
            )
        )
        movieDao.toggleFavorite(entity)
    }
}
