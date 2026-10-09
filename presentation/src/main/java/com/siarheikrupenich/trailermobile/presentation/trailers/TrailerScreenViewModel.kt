package com.siarheikrupenich.trailermobile.presentation.trailers

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.siarheikrupenich.trailermobile.core.network.data.NetworkError
import com.siarheikrupenich.trailermobile.domain.model.MovieCategory
import com.siarheikrupenich.trailermobile.presentation.data.TrailerState
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.usecase.GetFavoriteMoviesUiUseCase
import com.siarheikrupenich.trailermobile.presentation.usecase.GetTrailersByCategoryUiUseCase
import com.siarheikrupenich.trailermobile.presentation.usecase.ToggleFavoriteUiUseCase
import com.siarheikrupenich.trailermobile.viewmodel.ReducerSupportStreamViewModel
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrailerScreenViewModel @Inject constructor(
    getTrailersByCategoryUi: GetTrailersByCategoryUiUseCase,
    private val getFavoriteMoviesUi: GetFavoriteMoviesUiUseCase,
    private val toggleFavoriteUi: ToggleFavoriteUiUseCase
) : ReducerSupportStreamViewModel<TrailerUiEvent, MviTrailerState, TrailerState>(
    MviTrailerState(isLoading = true)
) {

    private val selectedCategory = MutableStateFlow(MovieCategory.NOW_PLAYING)

    val pagedTrailers: Flow<PagingData<TrailerUi>> = selectedCategory
        .flatMapLatest { category ->
            getTrailersByCategoryUi(category)
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            getFavoriteMoviesUi(Unit).collect { favorites ->
                nextEvent(TrailerUiEvent.FavoritesUpdated(favorites.map { it.id }.toSet()))
            }
        }

        query(TrailerUiEvent.LoadTrailers::class.java) { _ ->
            TrailerState.Loading
        }

        query(TrailerUiEvent.SelectCategory::class.java) { event ->
            selectedCategory.value = event.category
            TrailerState.CategoryChanged(event.category)
        }

        query(TrailerUiEvent.FavoritesUpdated::class.java) { event ->
            TrailerState.FavoritesChanged(event.favoriteIds)
        }

        command(TrailerUiEvent.ToggleFavorite::class.java) { event ->
            toggleFavoriteUi(event.movie)
        }

        reducer(TrailerState.CategoryChanged::class.java) { state, result ->
            state.copy(selectedCategory = result.category)
        }

        reducer(TrailerState.FavoritesChanged::class.java) { state, result ->
            state.copy(favoriteIds = result.favoriteIds)
        }

        reducer(TrailerState.Loading::class.java) { state, _ ->
            state.copy(isLoading = true, error = null)
        }

        reducer(TrailerState.Success::class.java) { state, result ->
            state.copy(
                isLoading = false,
                isEmpty = result.trailers.isEmpty(),
                trailers = result.trailers,
                error = null
            )
        }

        reducer(TrailerState.Error::class.java) { state, result ->
            state.copy(
                isLoading = false,
                error = result.error
            )
        }

        reducer(TrailerState.Empty::class.java) { state, _ ->
            state.copy(
                isLoading = false,
                isEmpty = true
            )
        }

        nextEvent(TrailerUiEvent.LoadTrailers(isRefreshing = false))
    }
}

sealed class TrailerUiEvent : UiEvent {
    data class LoadTrailers(val isRefreshing: Boolean) : TrailerUiEvent()
    data class SelectCategory(val category: MovieCategory) : TrailerUiEvent()
    data class FavoritesUpdated(val favoriteIds: Set<Long>) : TrailerUiEvent()
    data class ToggleFavorite(val movie: TrailerUi) : TrailerUiEvent()
}

data class MviTrailerState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val selectedCategory: MovieCategory = MovieCategory.NOW_PLAYING,
    val favoriteIds: Set<Long> = emptySet(),
    val trailers: List<TrailerUi>? = null,
    val error: NetworkError? = null
)
