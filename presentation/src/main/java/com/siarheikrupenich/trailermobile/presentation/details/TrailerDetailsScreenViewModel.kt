package com.siarheikrupenich.trailermobile.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.siarheikrupenich.trailermobile.core.network.data.ResultWithFallback
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.usecase.GetMovieDetailsUiParams
import com.siarheikrupenich.trailermobile.presentation.usecase.GetMovieDetailsUiUseCase
import com.siarheikrupenich.trailermobile.viewmodel.ReducerSupportStreamViewModel
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrailerDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUi: GetMovieDetailsUiUseCase
) : ReducerSupportStreamViewModel<TrailerDetailsUiEvent, TrailerDetailsState, TrailerDetailsResult>(
    TrailerDetailsState(isLoading = true)
) {
    val movieId: Long = savedStateHandle.get<Long>("movieId") ?: 0L

    init {
        query(TrailerDetailsUiEvent.LoadMovieDetails::class.java) { event ->
            when (val result = getMovieDetailsUi(GetMovieDetailsUiParams(event.movieId))) {
                is ResultWithFallback.Success -> TrailerDetailsResult.Success(result.data)
                is ResultWithFallback.Failure -> TrailerDetailsResult.Error(
                    result.error.message ?: "Failed to load movie details"
                )
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

        nextEvent(TrailerDetailsUiEvent.LoadMovieDetails(movieId))
    }
}

sealed class TrailerDetailsUiEvent : UiEvent {
    data class LoadMovieDetails(val movieId: Long) : TrailerDetailsUiEvent()
}

sealed class TrailerDetailsResult {
    data class Success(val movie: TrailerUi) : TrailerDetailsResult()
    data class Error(val error: String) : TrailerDetailsResult()
}

data class TrailerDetailsState(
    val isLoading: Boolean = false,
    val movie: TrailerUi? = null,
    val releaseYear: String = "",
    val error: String? = null
)
