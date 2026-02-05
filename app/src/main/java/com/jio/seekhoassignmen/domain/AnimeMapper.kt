package com.jio.seekhoassignmen.domain

import com.jio.seekhoassignmen.data.local.AnimeEntity
import com.jio.seekhoassignmen.data.remote.AnimeDto
import com.jio.seekhoassignmen.data.remote.AnimeDetailDto

fun AnimeDto.toEntity() = AnimeEntity(
    id = mal_id,
    title = title,
    episodes = episodes,
    score = score,
    imageUrl = images.jpg.image_url
)

fun AnimeDetailDto.toEntity() = AnimeEntity(
    id = mal_id,
    title = title,
    episodes = episodes,
    score = score,
    imageUrl = images.jpg.image_url,
    synopsis = synopsis,
    genres = genres?.joinToString(",") { it.name } ?: "",
    trailerUrl = trailer?.embed_url
)

fun AnimeEntity.toDomain() = Anime(
    id = id,
    title = title,
    episodes = episodes,
    score = score,
    imageUrl = imageUrl
)

fun AnimeDetailDto.toDomainDetail() = AnimeDetail(
    mal_id = mal_id,
    title = title,
    title_english = title_english,
    title_japanese = title_japanese,
    episodes = episodes,
    score = score,
    rating = rating,
    synopsis = synopsis,
    posterUrl = images.jpg.image_url,
    trailerEmbedUrl = trailer?.embed_url,
    genres = genres?.map { it.name } ?: emptyList(),
    airedString = aired?.string)
fun AnimeEntity.toDomainDetail() = AnimeDetail(
    mal_id = id,
    title = title,
    title_english = null,
    title_japanese = null,
    episodes = episodes,
    score = score,
    rating = null,
    synopsis = synopsis,
    posterUrl = imageUrl,
    trailerEmbedUrl = trailerUrl,
    genres = genres?.split(",")?.map { it.trim() } ?: emptyList(),
    airedString = null
)