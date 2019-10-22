package com.example.moviefinder.ui.movies

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.moviefinder.R
import com.example.moviefinder.networking.DiscoverMovie
import com.example.moviefinder.utils.Constants
import com.example.moviefinder.utils.PosterSize
import com.example.moviefinder.utils.load
import kotlinx.android.synthetic.main.view_movies.view.*

class MoviesAdapter(private var interaction: Interaction? = null) :
    ListAdapter<DiscoverMovie, RecyclerView.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<DiscoverMovie>(){
        override fun areContentsTheSame(oldItem: DiscoverMovie, newItem: DiscoverMovie): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: DiscoverMovie, newItem: DiscoverMovie): Boolean {
            return oldItem.id == newItem.id
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiscoverMoviesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_movies,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiscoverMoviesViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }


    fun setInteraction(interaction:Interaction){
        this.interaction = interaction
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