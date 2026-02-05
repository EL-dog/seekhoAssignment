package com.jio.seekhoassignmen.domain

data class AnimeDetail(
    val mal_id: Int,
    val title: String,
    val title_english: String?,
    val title_japanese: String?,
    val episodes: Int?,
    val score: Double?,
    val rating: String?,
    val synopsis: String?,
    val posterUrl: String?,
    val trailerEmbedUrl: String?,
    val genres: List<String>,
    val airedString: String?
)
