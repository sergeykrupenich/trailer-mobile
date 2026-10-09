package com.siarheikrupenich.trailermobile.domain.usecase

import com.siarheikrupenich.trailermobile.core.UseCase
import com.siarheikrupenich.trailermobile.domain.repository.TrailerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: TrailerRepository
) : UseCase<Long, Flow<Boolean>> {

    override fun invoke(param: Long): Flow<Boolean> {
        return repository.isFavorite(param)
    }
}
