package com.siarheikrupenich.trailermobile.data.repository

import app.cash.turbine.test
import com.siarheikrupenich.trailermobile.data.api.TrailerApi
import com.siarheikrupenich.trailermobile.data.db.dao.MovieDao
import com.siarheikrupenich.trailermobile.data.db.entity.MovieEntity
import com.siarheikrupenich.trailermobile.data.mapper.MovieDtoToEntityMapper
import com.siarheikrupenich.trailermobile.data.mapper.MovieEntityToDtoMapper
import com.siarheikrupenich.trailermobile.domain.repository.dto.MovieDto
import com.siarheikrupenich.trailermobile.domain.repository.dto.NowPlayingResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TrailerRepositoryImplTest {

    private lateinit var trailerApi: TrailerApi
    private lateinit var movieDao: MovieDao
    private lateinit var dtoToEntityMapper: MovieDtoToEntityMapper
    private lateinit var entityToDtoMapper: MovieEntityToDtoMapper

    private lateinit var repository: TrailerRepositoryImpl

    @Before
    fun setUp() {
        trailerApi = mockk()
        movieDao = mockk(relaxed = true)
        dtoToEntityMapper = mockk()
        entityToDtoMapper = mockk()

        repository = TrailerRepositoryImpl(
            trailerApi = trailerApi,
            movieDao = movieDao,
            dtoToEntityMapper = dtoToEntityMapper,
            entityToDtoMapper = entityToDtoMapper
        )
    }

    @Test
    fun `getMovies success fetches from api, maps, caches, and returns response`() = runTest {
        val category = "now_playing"
        val page = 1
        val movieDto = MovieDto(id = 1L, title = "Movie 1", overview = "Overview 1")
        val responseDto = NowPlayingResponseDto(page = 1, results = listOf(movieDto), totalPages = 1, totalResults = 1)
        val movieEntity = MovieEntity(
            id = 1L,
            title = "Movie 1",
            overview = "Overview 1",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = category,
            orderIndex = 0,
            isFavorite = false
        )

        coEvery { trailerApi.getMovies(category = category, page = page) } returns responseDto
        every { dtoToEntityMapper.map(any()) } returns movieEntity

        val result = repository.getMovies(category, page)

        assertEquals(responseDto, result)
        coVerify { trailerApi.getMovies(category = category, page = page) }
        coVerify { movieDao.insertMoviesPreservingFavorites(listOf(movieEntity)) }
    }

    @Test
    fun `getMovies error falls back to cache when cache is not empty`() = runTest {
        val category = "now_playing"
        val page = 1
        val movieEntity = MovieEntity(
            id = 1L,
            title = "Cached Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = category,
            orderIndex = 0,
            isFavorite = false
        )
        val movieDto = MovieDto(id = 1L, title = "Cached Movie", overview = "Overview")

        coEvery { trailerApi.getMovies(category = category, page = page) } throws RuntimeException("Network error")
        coEvery { movieDao.getMoviesByCategorySync(category) } returns listOf(movieEntity)
        every { entityToDtoMapper.map(movieEntity) } returns movieDto

        val result = repository.getMovies(category, page)

        assertEquals(1, result.results.size)
        assertEquals("Cached Movie", result.results[0].title)
        coVerify { movieDao.getMoviesByCategorySync(category) }
    }

    @Test(expected = RuntimeException::class)
    fun `getMovies error rethrows when cache is empty`() = runTest {
        val category = "now_playing"
        val page = 1

        coEvery { trailerApi.getMovies(category = category, page = page) } throws RuntimeException("Network error")
        coEvery { movieDao.getMoviesByCategorySync(category) } returns emptyList()

        repository.getMovies(category, page)
    }

    @Test
    fun `getMovieDetails success fetches from api, updates cache, and returns dto`() = runTest {
        val movieId = 1L
        val movieDto = MovieDto(id = movieId, title = "Detail Movie", overview = "Overview")
        val cachedEntity = MovieEntity(
            id = movieId,
            title = "Old Title",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = "popular",
            orderIndex = 2,
            isFavorite = true
        )
        val mappedEntity = MovieEntity(
            id = movieId,
            title = "Detail Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = "popular",
            orderIndex = 2,
            isFavorite = true
        )

        coEvery { trailerApi.getMovieDetails(movieId = movieId) } returns movieDto
        coEvery { movieDao.getMovieById(movieId) } returns cachedEntity
        every { dtoToEntityMapper.map(any()) } returns mappedEntity

        val result = repository.getMovieDetails(movieId)

        assertEquals(movieDto, result)
        coVerify { trailerApi.getMovieDetails(movieId = movieId) }
        coVerify { movieDao.insertMovie(mappedEntity) }
    }

    @Test
    fun `getMovieDetails error falls back to cache when present`() = runTest {
        val movieId = 1L
        val cachedEntity = MovieEntity(
            id = movieId,
            title = "Cached Detail",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = "popular",
            orderIndex = 2,
            isFavorite = true
        )
        val movieDto = MovieDto(id = movieId, title = "Cached Detail", overview = "Overview")

        coEvery { trailerApi.getMovieDetails(movieId = movieId) } throws RuntimeException("Network error")
        coEvery { movieDao.getMovieById(movieId) } returns cachedEntity
        every { entityToDtoMapper.map(cachedEntity) } returns movieDto

        val result = repository.getMovieDetails(movieId)

        assertEquals(movieDto, result)
    }

    @Test(expected = RuntimeException::class)
    fun `getMovieDetails error rethrows when cache is null`() = runTest {
        val movieId = 1L

        coEvery { trailerApi.getMovieDetails(movieId = movieId) } throws RuntimeException("Network error")
        coEvery { movieDao.getMovieById(movieId) } returns null

        repository.getMovieDetails(movieId)
    }

    @Test
    fun `getFavoriteMovies emits mapped favorite movies`() = runTest {
        val movieEntity = MovieEntity(
            id = 1L,
            title = "Fav",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = "now_playing",
            orderIndex = 0,
            isFavorite = true
        )
        val movieDto = MovieDto(id = 1L, title = "Fav", overview = "Overview")

        every { movieDao.getFavoriteMovies() } returns flowOf(listOf(movieEntity))
        every { entityToDtoMapper.map(movieEntity) } returns movieDto

        repository.getFavoriteMovies().test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Fav", items[0].title)
            awaitComplete()
        }
    }

    @Test
    fun `isFavorite emits favorite status`() = runTest {
        val movieId = 1L
        every { movieDao.isFavorite(movieId) } returns flowOf(true)

        repository.isFavorite(movieId).test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `toggleFavorite toggles favorite state`() = runTest {
        val movieDto = MovieDto(id = 1L, title = "Movie", overview = "Overview")
        val cachedEntity = MovieEntity(
            id = 1L,
            title = "Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = "now_playing",
            orderIndex = 0,
            isFavorite = false
        )
        val toggledEntity = MovieEntity(
            id = 1L,
            title = "Movie",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            voteAverage = 0.0,
            releaseDate = "2023-01-01",
            category = "now_playing",
            orderIndex = 0,
            isFavorite = true
        )

        coEvery { movieDao.getMovieById(movieDto.id) } returns cachedEntity
        every { dtoToEntityMapper.map(any()) } returns toggledEntity

        repository.toggleFavorite(movieDto)

        coVerify { movieDao.toggleFavorite(toggledEntity) }
    }
}
