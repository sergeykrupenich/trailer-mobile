package com.siarheikrupenich.trailermobile.core.network.data

sealed class NetworkError(val message: String? = null) {

    data object NetworkLost : NetworkError("No internet connection")

    data object Unauthorized : NetworkError("Unauthorized access")

    data class Unknown(val error: Throwable) : NetworkError(error.message)
}

class NetworkException(val error: NetworkError) : RuntimeException(
    error.message,
    (error as? NetworkError.Unknown)?.error
)
