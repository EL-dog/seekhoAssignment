package com.jio.seekhoassignmen.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jio.seekhoassignmen.R
import com.jio.seekhoassignmen.adapter.AnimelistAdapter
import com.jio.seekhoassignmen.databinding.FragmentHomePageBinding
import com.jio.seekhoassignmen.utils.EndlessScrollListener
import com.jio.seekhoassignmen.viewmodel.AnimeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomePageFragment : Fragment(R.layout.fragment_home_page) {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnimeListViewModel by viewModels()
    private val adapter = AnimelistAdapter { anime ->
        navigateToDetail(anime.id)
    }

    private lateinit var scrollListener: EndlessScrollListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomePageBinding.bind(view)


        view.alpha = 0f
        view.translationY = 40f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = this@HomePageFragment.adapter
            setHasFixedSize(true)
        }

        scrollListener = object : EndlessScrollListener(layoutManager, visibleThreshold = 5) {
            override fun onLoadMore() {
                viewModel.loadNextPage()
            }
        }

        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    private fun observeViewModel() {
        var previousSize = 0

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.animeList.collect { list ->
                if (previousSize == 0) {
                    adapter.submitList(list)
                } else if (list.size > previousSize) {
                    adapter.appendList(list.drop(previousSize))
                }
                previousSize = list.size
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isPaginating.collect {
                scrollListener.setLoading(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hasNextPage.collect {
                scrollListener.setLastPage(!it)
            }
        }
    }

    private fun navigateToDetail(animeId: Int) {
        val detailFragment = AnimeDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("anime_id", animeId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
