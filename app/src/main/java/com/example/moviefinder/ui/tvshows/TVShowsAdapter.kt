package com.example.moviefinder.ui.tvshows

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.moviefinder.R
import com.example.moviefinder.networking.Movie
import com.example.moviefinder.networking.TVShow
import com.example.moviefinder.utils.Constants
import com.example.moviefinder.utils.PosterSize
import com.example.moviefinder.utils.load
import com.example.moviefinder.utils.loadColor
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.view_movies.view.*

class TVShowsAdapter(private var interaction: Interaction? = null) :
    PagedListAdapter<TVShow, RecyclerView.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<TVShow>(){
        override fun areContentsTheSame(oldItem: TVShow, newItem: TVShow): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: TVShow, newItem: TVShow): Boolean {
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

        fun bind(item: TVShow?) {
            if(item != null) {
                itemView.setOnClickListener {
                    interaction?.onItemSelected(adapterPosition, item)
                }
                itemView.img.load(
                    Constants.IMAGE_BASE_URL + PosterSize.w500 + item?.poster_path,
                    crop = true,
                    fade = true
                )
            }else {


                itemView.img.setImageResource(R.color.grayPlaceholder)
                d { "item null" }

            }


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: TVShow)
    }
}