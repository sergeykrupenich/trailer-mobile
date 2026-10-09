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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // TODO: add later a subscription if Internet is on
    // also, ensure only if Internet connection is off, the value is false.
    // Think about it to move out of the repository.
    private val _isOffline = MutableStateFlow(false)

    override fun getOfflineStatus(): Flow<Boolean> = _isOffline.asStateFlow()

    override suspend fun getMovies(category: String, page: Int): NowPlayingResponseDto {
        return try {
            val response = trailerApi.getMovies(category = category, page = page)
            val entities = response.results.map { dto ->
                dtoToEntityMapper.map(MovieDtoToEntityParams(dto = dto, category = category))
            }
            movieDao.insertMoviesPreservingFavorites(entities)
            _isOffline.value = false
            response
        } catch (e: Exception) {
            _isOffline.value = true
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
            val entity = dtoToEntityMapper.map(MovieDtoToEntityParams(movieDto))
            val cached = movieDao.getMovieById(movieId)
            if (cached != null) {
                movieDao.insertMovie(entity.copy(isFavorite = cached.isFavorite))
            } else {
                movieDao.insertMovie(entity)
            }
            _isOffline.value = false
            movieDto
        } catch (e: Exception) {
            _isOffline.value = true
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
        val entity = dtoToEntityMapper.map(MovieDtoToEntityParams(movie, isFavorite = !isFavoriteNow))
        movieDao.toggleFavorite(entity)
    }
}
