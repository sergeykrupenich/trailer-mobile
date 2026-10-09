package com.siarheikrupenich.trailermobile.domain.model

data class TrailerDomainModel(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String
)
