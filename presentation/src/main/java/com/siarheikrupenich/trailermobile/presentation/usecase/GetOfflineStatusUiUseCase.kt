package com.siarheikrupenich.trailermobile.presentation.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.usecase.GetOfflineStatusUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOfflineStatusUiUseCase @Inject constructor(
    private val getOfflineStatus: GetOfflineStatusUseCase
) : UseCase<Unit, Flow<Boolean>> {
    override fun invoke(param: Unit): Flow<Boolean> {
        return getOfflineStatus(Unit)
    }
}
