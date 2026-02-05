package com.jio.seekhoassignmen.domain

data class PaginationData(
    val current_page: Int,
    val last_visible_page: Int,
    val has_next_page: Boolean,
    val items: PaginationItems
)

data class PaginationItems(
    val count: Int,
    val total: Int,
    val per_page: Int
)
