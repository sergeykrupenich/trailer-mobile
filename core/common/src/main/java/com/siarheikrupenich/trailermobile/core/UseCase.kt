package com.siarheikrupenich.trailermobile.core

interface UseCase<in T, out O> {

    operator fun invoke(param: T): O
}
