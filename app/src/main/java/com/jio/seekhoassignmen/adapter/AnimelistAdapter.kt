package com.jio.seekhoassignmen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jio.seekhoassignmen.R
import com.jio.seekhoassignmen.domain.Anime

class AnimelistAdapter(
    private val onAnimeClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimelistAdapter.AnimeViewHolder>() {

    private var lastAnimatedPosition = -1


    private val items = mutableListOf<Anime>()

    fun submitList(list: List<Anime>) {
        val diffResult = DiffUtil.calculateDiff(AnimeDiffCallback(items, list))
        items.clear()
        items.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    fun appendList(list: List<Anime>) {
        val oldSize = items.size
        items.addAll(list)
        notifyItemRangeInserted(oldSize, list.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(items[position], onAnimeClick)
    }

    override fun onViewAttachedToWindow(holder: AnimeViewHolder) {
        super.onViewAttachedToWindow(holder)
        animateDrop(holder.itemView, holder.bindingAdapterPosition)
    }

    private fun animateDrop(view: android.view.View, position: Int) {
        if (position == RecyclerView.NO_POSITION) return
//        if (position <= lastAnimatedPosition) return

//        view.translationY = 250f

        view.alpha = 0f
        view.scaleX = 0.96f
        view.scaleY = 0.96f

        view.animate()
            .translationY(0f)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()


//        lastAnimatedPosition = position
    }


    override fun getItemCount(): Int = items.size

    class AnimeViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val poster = itemView.findViewById<ImageView>(R.id.imgPoster)
        private val title = itemView.findViewById<TextView>(R.id.tvTitle)
        private val episodes = itemView.findViewById<TextView>(R.id.tvEpisodes)
        private val rating = itemView.findViewById<TextView>(R.id.tvRating)

        fun bind(anime: Anime, onAnimeClick: (Anime) -> Unit) {
            title.text = anime.title
            episodes.text = "Episodes: ${anime.episodes}"
            rating.text = "Rating: ${anime.score}"

            Glide.with(itemView)
                .load(anime.imageUrl)
                .into(poster)

            itemView.setOnClickListener {
                onAnimeClick(anime)
            }
        }
    }

    private class AnimeDiffCallback(
        private val oldList: List<Anime>,
        private val newList: List<Anime>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}