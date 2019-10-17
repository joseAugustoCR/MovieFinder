package com.example.moviefinder.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.moviefinder.R
import com.example.moviefinder.networking.DiscoverMovie
import com.example.moviefinder.utils.Constants
import com.example.moviefinder.utils.PosterSize
import com.example.moviefinder.utils.load
import kotlinx.android.synthetic.main.view_discovermovies.view.*

class DiscoverMoviesAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DiscoverMovie>() {

        override fun areItemsTheSame(oldItem: DiscoverMovie, newItem: DiscoverMovie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiscoverMovie, newItem: DiscoverMovie): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return DiscoverMoviesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_discovermovies,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverMoviesViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<DiscoverMovie>) {
        differ.submitList(list)
    }

    class DiscoverMoviesViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: DiscoverMovie) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            img.load(Constants.IMAGE_BASE_URL + PosterSize.w500 + item.poster_path, crop = true, fade = true)

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: DiscoverMovie)
    }
}