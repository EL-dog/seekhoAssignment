package com.jio.seekhoassignmen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jio.seekhoassignmen.data.repo.AnimeRepository
import com.jio.seekhoassignmen.data.repo.Resource
import com.jio.seekhoassignmen.domain.AnimeDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId: Int = savedStateHandle.get<Int>("anime_id") ?: 0

    private val _animeDetail = MutableStateFlow<AnimeDetail?>(null)
    val animeDetail: StateFlow<AnimeDetail?> = _animeDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchAnimeDetails()
    }

    private fun fetchAnimeDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = repository.getAnimeDetails(animeId)
                when (result) {
                    is Resource.Success -> {
                        _animeDetail.value = result.data
                        _isLoading.value = false
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {
                        // Loading state handled
                        _isLoading.value = true
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch details: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun retry() {
        fetchAnimeDetails()
    }
}
