package com.example.app.ui.main.home

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.api.Product
import com.example.app.ui.MainActivity
import kotlin.math.roundToInt

class ProductsAdapter(private val interaction: Interaction? = null) :
    ListAdapter<Product, RecyclerView.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ProductsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_product,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductsViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    class ProductsViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Product) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            val displayMetrics = DisplayMetrics()
            itemView.context.display?.getMetrics(displayMetrics)
            var width = displayMetrics.widthPixels
            var itemWidth = width/5f
            updateLayoutParams {
                width = itemWidth.roundToInt()
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Product)
    }
}