package com.siarheikrupenich.trailermobile.presentation.data

data class TrailerUi(
    val id: Long,
    val title: String,
    val description: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val voteAverage: Double = 0.0,
    val releaseDate: String = ""
)
