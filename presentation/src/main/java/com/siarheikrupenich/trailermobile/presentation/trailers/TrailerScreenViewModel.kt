package com.siarheikrupenich.trailermobile.presentation.trailers

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.siarheikrupenich.trailermobile.core.network.data.NetworkError
import com.siarheikrupenich.trailermobile.domain.usecase.GetNowPlayingTrailersUseCase
import com.siarheikrupenich.trailermobile.presentation.data.TrailerState
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.presentation.mapper.TrailerDomainToUiMapper
import com.siarheikrupenich.trailermobile.viewmodel.ReducerSupportStreamViewModel
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TrailerScreenViewModel @Inject constructor(
    getNowPlayingTrailersUseCase: GetNowPlayingTrailersUseCase,
    mapper: TrailerDomainToUiMapper
) : ReducerSupportStreamViewModel<TrailerUiEvent, MviTrailerState, TrailerState>(
    MviTrailerState(isLoading = true)
) {

    val pagedTrailers: Flow<PagingData<TrailerUi>> = getNowPlayingTrailersUseCase()
        .map { pagingData ->
            pagingData.map { domainModel -> mapper.map(domainModel) }
        }
        .cachedIn(viewModelScope)

    init {
        query(TrailerUiEvent.LoadTrailers::class.java) { _ ->
            TrailerState.Loading
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
}

data class MviTrailerState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val trailers: List<TrailerUi>? = null,
    val error: NetworkError? = null
)
