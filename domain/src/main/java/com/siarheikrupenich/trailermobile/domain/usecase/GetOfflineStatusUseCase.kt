package com.siarheikrupenich.trailermobile.domain.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.repository.NetworkConnectivityObserver
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOfflineStatusUseCase @Inject constructor(
    private val connectivityObserver: NetworkConnectivityObserver
) : UseCase<Unit, Flow<Boolean>> {
    override fun invoke(param: Unit): Flow<Boolean> {
        return connectivityObserver.isOffline()
    }
}
