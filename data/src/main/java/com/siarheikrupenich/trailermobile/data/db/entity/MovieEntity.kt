package com.siarheikrupenich.trailermobile.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val category: String = "",
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
