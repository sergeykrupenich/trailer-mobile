package com.siarheikrupenich.trailermobile.presentation.details

import androidx.lifecycle.viewModelScope
import com.siarheikrupenich.trailermobile.core.network.data.ResultWithFallback
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.usecase.GetMovieDetailsUiParams
import com.siarheikrupenich.trailermobile.presentation.usecase.GetMovieDetailsUiUseCase
import com.siarheikrupenich.trailermobile.presentation.usecase.IsFavoriteUiUseCase
import com.siarheikrupenich.trailermobile.presentation.usecase.ToggleFavoriteUiUseCase
import com.siarheikrupenich.trailermobile.viewmodel.ReducerSupportStreamViewModel
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrailerDetailsScreenViewModel @Inject constructor(
    private val getMovieDetailsUi: GetMovieDetailsUiUseCase,
    private val isFavoriteUi: IsFavoriteUiUseCase,
    private val toggleFavoriteUi: ToggleFavoriteUiUseCase
) : ReducerSupportStreamViewModel<TrailerDetailsUiEvent, TrailerDetailsState, TrailerDetailsResult>(
    TrailerDetailsState(isLoading = true)
) {
    private var favoriteJob: Job? = null

    init {
        query(TrailerDetailsUiEvent.LoadMovieDetails::class.java) { event ->
            favoriteJob?.cancel()
            favoriteJob = viewModelScope.launch {
                isFavoriteUi(event.movieId).collect { isFav ->
                    sendResult(TrailerDetailsResult.FavoriteStatusChanged(isFav))
                }
            }

            when (val result = getMovieDetailsUi(GetMovieDetailsUiParams(event.movieId))) {
                is ResultWithFallback.Success -> TrailerDetailsResult.Success(result.data)
                is ResultWithFallback.Failure -> TrailerDetailsResult.Error(
                    result.error.message ?: "Failed to load movie details"
                )
            }
        }

        command(TrailerDetailsUiEvent.ToggleFavorite::class.java) { event ->
            event.movie?.let { movie ->
                toggleFavoriteUi(movie)
            }
        }

        reducer(TrailerDetailsResult.Success::class.java) { state, result ->
            val releaseYear = result.movie.releaseDate.takeIf { it.isNotBlank() }?.let { date ->
                if (date.length >= 4) date.substring(0, 4) else date
            } ?: ""
            state.copy(
                isLoading = false,
                movie = result.movie,
                releaseYear = releaseYear,
                error = null
            )
        }

        reducer(TrailerDetailsResult.Error::class.java) { state, result ->
            state.copy(isLoading = false, error = result.error)
        }

        reducer(TrailerDetailsResult.FavoriteStatusChanged::class.java) { state, result ->
            state.copy(isFavorite = result.isFavorite)
        }
    }
}

sealed class TrailerDetailsUiEvent : UiEvent {
    data class LoadMovieDetails(val movieId: Long) : TrailerDetailsUiEvent()
    data class ToggleFavorite(val movie: TrailerUi?) : TrailerDetailsUiEvent()
}

sealed class TrailerDetailsResult {
    data class Success(val movie: TrailerUi) : TrailerDetailsResult()
    data class Error(val error: String) : TrailerDetailsResult()
    data class FavoriteStatusChanged(val isFavorite: Boolean) : TrailerDetailsResult()
}

data class TrailerDetailsState(
    val isLoading: Boolean = false,
    val movie: TrailerUi? = null,
    val releaseYear: String = "",
    val isFavorite: Boolean = false,
    val error: String? = null
)
