package com.siarheikrupenich.trailermobile.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.siarheikrupenich.trailermobile.data.db.dao.MovieDao
import com.siarheikrupenich.trailermobile.data.db.entity.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TrailerDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
