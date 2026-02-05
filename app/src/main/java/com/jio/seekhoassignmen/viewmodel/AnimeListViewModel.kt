package com.jio.seekhoassignmen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jio.seekhoassignmen.data.repo.AnimeRepository
import com.jio.seekhoassignmen.data.repo.Resource
import com.jio.seekhoassignmen.domain.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _animeList = MutableStateFlow<List<Anime>>(emptyList())
    val animeList: StateFlow<List<Anime>> = _animeList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isPaginating = MutableStateFlow(false)
    val isPaginating: StateFlow<Boolean> = _isPaginating

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _hasNextPage = MutableStateFlow(false)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    init {
        fetchAnime()
    }

    private fun fetchAnime() {
        viewModelScope.launch {
            try {
                repository.getAnimeList().collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _isLoading.value = true
                            _error.value = null
                        }
                        is Resource.Success -> {
                            _animeList.value = resource.data
                            _isLoading.value = false
                            _error.value = null
                            _currentPage.value = 1
                            _hasNextPage.value = true
                        }
                        is Resource.Error -> {
                            _isLoading.value = false
                            _error.value = resource.message
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }

    fun loadNextPage() {
        if (_isPaginating.value || !_hasNextPage.value) {
            return
        }

        viewModelScope.launch {
            try {
                _isPaginating.value = true
                _error.value = null

                val result = repository.getNextPage(_currentPage.value)

                when (result) {
                    is Resource.Success -> {
                        val paginatedResponse = result.data
                        val updatedList = _animeList.value + paginatedResponse.data
                        _animeList.value = updatedList

                        _currentPage.value += 1
                        paginatedResponse.pagination?.let {
                            _hasNextPage.value = it.has_next_page
                            _totalItems.value = it.items.total
                        }

                        _isPaginating.value = false
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        _isPaginating.value = false
                    }
                    is Resource.Loading -> {
                        // Loading state handled
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isPaginating.value = false
            }
        }
    }
}


