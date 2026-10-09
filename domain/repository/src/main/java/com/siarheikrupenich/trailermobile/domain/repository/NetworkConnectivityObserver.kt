package com.siarheikrupenich.trailermobile.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityObserver {
    fun isOffline(): Flow<Boolean>
}
