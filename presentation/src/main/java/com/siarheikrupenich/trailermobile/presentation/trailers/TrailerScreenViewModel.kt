package com.siarheikrupenich.trailermobile.presentation.trailers

import com.siarheikrupenich.trailermobile.core.network.data.NetworkError
import com.siarheikrupenich.trailermobile.presentation.data.TrailerState
import com.siarheikrupenich.trailermobile.presentation.data.TrailerUi
import com.siarheikrupenich.trailermobile.viewmodel.ReducerSupportStreamViewModel
import com.siarheikrupenich.trailermobile.viewmodel.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrailerScreenViewModel @Inject constructor(
): ReducerSupportStreamViewModel<TrailerUiEvent, MviTrailerState, TrailerState>(
    MviTrailerState(isLoading = true)
) {
    // TODO
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
