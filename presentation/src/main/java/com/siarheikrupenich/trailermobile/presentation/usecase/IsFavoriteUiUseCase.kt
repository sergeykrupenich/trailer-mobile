package com.siarheikrupenich.trailermobile.presentation.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.usecase.IsFavoriteUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUiUseCase @Inject constructor(
    private val isFavorite: IsFavoriteUseCase
) : UseCase<Long, Flow<Boolean>> {

    override fun invoke(param: Long): Flow<Boolean> {
        return isFavorite(param)
    }
}
