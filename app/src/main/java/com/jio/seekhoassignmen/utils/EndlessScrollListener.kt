package com.jio.seekhoassignmen.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val visibleThreshold: Int = 5
) : RecyclerView.OnScrollListener() {

    private var isLoading = false
    private var isLastPage = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()


        if (!isLoading && !isLastPage) {

            if (totalItemCount - lastVisibleItemPosition <= visibleThreshold) {
                isLoading = true
                onLoadMore()
            }
        }
    }

    abstract fun onLoadMore()

    fun setLoading(loading: Boolean) {
        isLoading = loading
    }

    fun setLastPage(isLast: Boolean) {
        isLastPage = isLast
    }
}
