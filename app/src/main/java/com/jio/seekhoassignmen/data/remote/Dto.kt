package com.jio.seekhoassignmen.data.remote

data class ApiResponse<T>(
    val data: T,
    val pagination: PaginationDto? = null
)

data class PaginationDto(
    val last_visible_page: Int,
    val has_next_page: Boolean,
    val current_page: Int,
    val items: PaginationItemsDto
)

data class PaginationItemsDto(
    val count: Int,
    val total: Int,
    val per_page: Int
)

data class AnimeDto(
    val mal_id: Int,
    val title: String,
    val episodes: Int?,
    val score: Double?,
    val images: ImagesDto
)

data class ImagesDto(
    val jpg: ImageJpg
)

data class ImageJpg(
    val image_url: String
)

// Anime Detail DTOs
data class AnimeDetailDto(
    val mal_id: Int,
    val title: String,
    val title_english: String?,
    val title_japanese: String?,
    val episodes: Int?,
    val score: Double?,
    val rating: String?,
    val synopsis: String?,
    val images: ImagesDto,
    val trailer: TrailerDto?,
    val genres: List<GenreDto>?,
    val aired: AiredDto?
)

data class TrailerDto(
    val youtube_id: String?,
    val url: String?,
    val embed_url: String?,
    val images: TrailerImagesDto?
)

data class TrailerImagesDto(
    val image_url: String?,
    val small_image_url: String?,
    val medium_image_url: String?,
    val large_image_url: String?,
    val maximum_image_url: String?
)

data class GenreDto(
    val mal_id: Int,
    val type: String,
    val name: String,
    val url: String
)

data class AiredDto(
    val from: String?,
    val to: String?,
    val string: String?
)

data class ApiDetailResponse<T>(
    val data: T
)

