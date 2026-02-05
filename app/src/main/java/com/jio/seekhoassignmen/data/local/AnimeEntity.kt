package com.jio.seekhoassignmen.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val episodes: Int?,
    val score: Double?,
    val imageUrl: String?,
    val synopsis: String? = null,
    val genres: String = "",
    val trailerUrl: String? = null
)
