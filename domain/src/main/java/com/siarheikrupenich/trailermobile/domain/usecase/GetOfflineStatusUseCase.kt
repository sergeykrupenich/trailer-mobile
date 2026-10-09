package com.siarheikrupenich.trailermobile.domain.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOfflineStatusUseCase @Inject constructor(
    private val repository: TrailerRepository
) : UseCase<Unit, Flow<Boolean>> {
    override fun invoke(param: Unit): Flow<Boolean> {
        return repository.getOfflineStatus()
    }
}
