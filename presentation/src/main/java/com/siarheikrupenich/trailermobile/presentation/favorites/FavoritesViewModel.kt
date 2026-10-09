package com.siarheikrupenich.trailermobile.presentation.favorites

import androidx.lifecycle.viewModelScope
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.usecase.GetFavoriteMoviesUiUseCase
import com.siarheikrupenich.trailermobile.presentation.usecase.ToggleFavoriteUiUseCase
import com.siarheikrupenich.trailermobile.viewmodel.ReducerSupportStreamViewModel
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteMoviesUi: GetFavoriteMoviesUiUseCase,
    private val toggleFavoriteUi: ToggleFavoriteUiUseCase
) : ReducerSupportStreamViewModel<FavoritesUiEvent, FavoritesState, FavoritesResult>(
    FavoritesState(isLoading = true)
) {
    init {
        viewModelScope.launch {
            getFavoriteMoviesUi(Unit).collect { favorites ->
                nextEvent(FavoritesUiEvent.FavoritesUpdated(favorites))
            }
        }

        query(FavoritesUiEvent.FavoritesUpdated::class.java) { event ->
            FavoritesResult.Success(event.favorites)
        }

        command(FavoritesUiEvent.ToggleFavorite::class.java) { event ->
            toggleFavoriteUi(event.movie)
        }

        reducer(FavoritesResult.Success::class.java) { state, result ->
            state.copy(isLoading = false, favorites = result.favorites)
        }
    }
}

sealed class FavoritesUiEvent : UiEvent {
    data class FavoritesUpdated(val favorites: List<TrailerUi>) : FavoritesUiEvent()
    data class ToggleFavorite(val movie: TrailerUi) : FavoritesUiEvent()
}

sealed class FavoritesResult {
    data class Success(val favorites: List<TrailerUi>) : FavoritesResult()
}

data class FavoritesState(
    val isLoading: Boolean = false,
    val favorites: List<TrailerUi> = emptyList()
)
