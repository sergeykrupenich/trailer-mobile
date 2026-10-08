package com.siarheikrupenich.trailermobile.core

fun interface SuspendUseCase<in T, out O> {

    suspend operator fun invoke(param: T): O
}
