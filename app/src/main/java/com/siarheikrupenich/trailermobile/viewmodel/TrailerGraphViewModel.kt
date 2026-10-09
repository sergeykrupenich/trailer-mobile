package com.siarheikrupenich.trailermobile.viewmodel

import androidx.lifecycle.ViewModel
import com.siarheikrupenich.trailermobile.domain.usecase.GetOfflineStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
// TODO: improve it in accordance with MVI manner, inheriting from ReducerSupportStreamViewModel
class TrailerGraphViewModel @Inject constructor(
    getOfflineStatus: GetOfflineStatusUseCase
) : ViewModel() {

    // TODO: add later a subscription if Internet is on
    val isOffline: Flow<Boolean> = getOfflineStatus(Unit)
}
