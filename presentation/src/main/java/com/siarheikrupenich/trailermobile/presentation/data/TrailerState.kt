package com.siarheikrupenich.trailermobile.presentation.data

import com.siarheikrupenich.trailermobile.core.network.data.NetworkError

sealed class TrailerState {
    data object Loading : TrailerState()
    data object Empty : TrailerState()
    data class Error(val error: NetworkError, val trailers: List<TrailerUi>? = null) : TrailerState()
    data class Success(val trailers: List<TrailerUi>) : TrailerState()
}
