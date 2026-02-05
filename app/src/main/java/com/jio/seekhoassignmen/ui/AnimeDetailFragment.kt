package com.jio.seekhoassignmen.ui

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jio.seekhoassignmen.R
import com.jio.seekhoassignmen.databinding.FragmentAnimeDetailBinding
import com.jio.seekhoassignmen.domain.AnimeDetail
import com.jio.seekhoassignmen.viewmodel.AnimeDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AnimeDetailFragment : Fragment(R.layout.fragment_anime_detail) {

    private val viewModel: AnimeDetailsViewModel by viewModels()

    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAnimeDetailBinding.bind(view)


        view.alpha = 0f
        view.translationY = 30f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(250)
            .setInterpolator(DecelerateInterpolator())
            .start()

        setupWebView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupWebView() = with(binding) {
        trailerWebView.webViewClient = WebViewClient()
        trailerWebView.settings.apply {
            javaScriptEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    private fun setupClickListeners() = with(binding) {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.animeDetail.collect { detail ->
                detail?.let { displayAnimeDetail(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.loadingProgressBar.visibility =
                    if (it) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect {
                if (it != null) showError(it) else hideError()
            }
        }
    }

    private fun displayAnimeDetail(detail: AnimeDetail) = with(binding) {
        tvTitle.text = detail.title
        tvEnglishTitle.text = detail.title_english?.takeIf { it.isNotEmpty() } ?: "N/A"
        tvScore.text = detail.score?.toString() ?: "N/A"
        tvEpisodes.text = detail.episodes?.toString() ?: "N/A"
        tvRating.text = detail.rating ?: "Not Rated"
        tvAired.text = detail.airedString ?: "Unknown"
        tvSynopsis.text = detail.synopsis ?: "No synopsis available"

        genreChipGroup.removeAllViews()
        detail.genres.forEach { genre ->
            genreChipGroup.addView(
                Chip(requireContext()).apply {
                    text = genre
                    isClickable = false
                    isCheckable = false
                }
            )
        }

        displayTrailerOrPoster(detail)
    }

    private fun displayTrailerOrPoster(detail: AnimeDetail) = with(binding) {
        if (!detail.trailerEmbedUrl.isNullOrEmpty()) {
            trailerProgressBar.visibility = View.VISIBLE

            val html = """
                <html>
                <body style="margin:0">
                    <iframe 
                        src="${detail.trailerEmbedUrl}" 
                        width="100%" 
                        height="100%" 
                        frameborder="0" 
                        allowfullscreen>
                    </iframe>
                </body>
                </html>
            """.trimIndent()

            trailerWebView.loadData(html, "text/html", "utf-8")
            trailerWebView.visibility = View.VISIBLE
            posterImage.visibility = View.GONE
            trailerProgressBar.visibility = View.GONE
        } else {
            trailerWebView.visibility = View.GONE
            posterImage.visibility = View.VISIBLE

            Glide.with(this@AnimeDetailFragment)
                .load(detail.posterUrl)
                .placeholder(android.R.drawable.ic_dialog_info)
                .error(android.R.drawable.ic_dialog_alert)
                .into(posterImage)

            trailerProgressBar.visibility = View.GONE
        }
    }

    private fun showError(error: String) = with(binding) {
        tvError.text = error
        tvError.visibility = View.VISIBLE
        btnRetry.visibility = View.VISIBLE
    }

    private fun hideError() = with(binding) {
        tvError.visibility = View.GONE
        btnRetry.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
