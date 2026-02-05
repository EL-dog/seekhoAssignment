package com.jio.seekhoassignmen.domain

data class Anime(
    val id: Int,
    val title: String,
    val episodes: Int?,
    val score: Double?,
    val imageUrl: String?
)
