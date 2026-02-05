package com.jio.seekhoassignmen.data.repo

import com.jio.seekhoassignmen.data.remote.AnimeApi
import com.jio.seekhoassignmen.data.local.AnimeDao
import com.jio.seekhoassignmen.domain.Anime
import com.jio.seekhoassignmen.domain.PaginationData
import com.jio.seekhoassignmen.domain.PaginationItems
import com.jio.seekhoassignmen.domain.toDomain
import com.jio.seekhoassignmen.domain.toDomainDetail
import com.jio.seekhoassignmen.domain.toEntity
import com.jio.seekhoassignmen.utils.NetworkMonitor
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnimeRepository @Inject constructor(
    private val api: AnimeApi,
    private val dao: AnimeDao,
    private val networkMonitor: NetworkMonitor
) {
    private val ITEMS_PER_PAGE = 25

    fun getAnimeList(): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading)

        try {
            val cached = dao.getAllAnimeList()
            
            if (cached.isNotEmpty()) {
                emit(Resource.Success(cached.map { it.toDomain() }))
            } else {
                val isOnline = networkMonitor.isOnline()
                
                if (isOnline) {
                    val response = api.getTopAnime(page = 1, limit = ITEMS_PER_PAGE)
                    val entities = response.data.map { it.toEntity() }
                    dao.insertAll(entities)
                    emit(Resource.Success(entities.map { it.toDomain() }))
                } else {
                    emit(Resource.Error("No cached data and no internet connection"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch anime: ${e.message}"))
        }
    }

    suspend fun getNextPage(currentPage: Int): Resource<PaginatedResponse> {
        return try {
            val nextPage = currentPage + 1
            
            if (!networkMonitor.isOnline()) {
                return Resource.Error("No internet connection")
            }

            val response = api.getTopAnime(page = nextPage, limit = ITEMS_PER_PAGE)
            val entities = response.data.map { it.toEntity() }
            dao.insertAll(entities)

            val animeList = entities.map { it.toDomain() }
            val paginationData = response.pagination?.let {
                PaginationData(
                    current_page = it.current_page,
                    last_visible_page = it.last_visible_page,
                    has_next_page = it.has_next_page,
                    items = PaginationItems(
                        count = it.items.count,
                        total = it.items.total,
                        per_page = it.items.per_page
                    )
                )
            }

            Resource.Success(
                PaginatedResponse(
                    data = animeList,
                    pagination = paginationData
                )
            )
        } catch (e: Exception) {
            Resource.Error("Failed to fetch page: ${e.message}")
        }
    }

    suspend fun getAnimeDetails(animeId: Int): Resource<com.jio.seekhoassignmen.domain.AnimeDetail> {
        return try {
            val cached = dao.getAnimeByIdSuspend(animeId)
            
            if (cached != null && !cached.synopsis.isNullOrEmpty()) {
                return Resource.Success(cached.toDomainDetail())
            } else if (cached != null) {
                dao.deleteById(animeId)
            }
            
            if (!networkMonitor.isOnline()) {
                if (cached != null) {
                    return Resource.Success(cached.toDomainDetail())
                }
                return Resource.Error("No internet connection")
            }

            val response = api.getAnimeDetails(animeId)
            val entity = response.data.toEntity()
            dao.insertAll(listOf(entity))

            val detail = response.data.toDomainDetail()
            Resource.Success(detail)
        } catch (e: Exception) {
            Resource.Error("Failed to fetch anime details: ${e.message}")
        }
    }
}

data class PaginatedResponse(
    val data: List<Anime>,
    val pagination: PaginationData?
)