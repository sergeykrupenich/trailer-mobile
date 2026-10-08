package com.siarheikrupenich.trailermobile.core

interface Mapper<in T, out O> {

    fun map(param: T): O
}
