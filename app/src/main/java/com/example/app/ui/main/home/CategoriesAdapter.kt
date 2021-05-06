package com.example.app.ui.main.home

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.app.R
import com.example.app.api.Category
import com.example.app.utils.views.ScrollStateHolder
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.view_category.view.*

class CategoriesAdapter(private val interaction: Interaction? = null, var scrollStates: MutableMap<Int, Parcelable?> = mutableMapOf(),
var scrollStateHolder: ScrollStateHolder?) :
    ListAdapter<Category, RecyclerView.ViewHolder>(DiffCallback()) {

    private val viewPool = RecyclerView.RecycledViewPool()
//    var scrollXState : IntArray? = IntArray(0)
//    var adapters : ArrayList<ProductsAdapter?> = ArrayList<ProductsAdapter?>()


    init {
        d{"init"}
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {

        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }


    override fun getItemId(position: Int): Long {
        return currentList.get(position).id?.toLong() ?: 0.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val holder=  CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_category,
                parent,
                false
            ),
            interaction
        )

        holder.onCreate()
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoriesViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    inner class CategoriesViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView), ScrollStateHolder.ScrollStateKeyProvider {

        fun onCreate(){
        }

        fun onRecycled(){
            scrollStateHolder?.saveScrollState(recyclerView = itemView.recyclerItems, this)
        }

        fun bind(item: Category) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            title.text = item.title
            subtitle.text = item.subtitle
            val childLayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            childLayoutManager.initialPrefetchItemCount = 4
            var productsAdapter = ProductsAdapter()
            itemView.recyclerItems.apply {
                layoutManager = childLayoutManager
                setHasFixedSize(true)
                adapter = productsAdapter
            }
            scrollStateHolder?.setupRecyclerView(itemView.recyclerItems, this@CategoriesViewHolder)
            productsAdapter.submitList(item.items)
            scrollStateHolder?.restoreScrollState(itemView.recyclerItems, this@CategoriesViewHolder)
//            restoreHorizontalScroll(item)

        }


        fun restoreHorizontalScroll(item:Category){
            val key = item.id
            val state = scrollStates[key]
            if(state != null){
                itemView.recyclerItems.layoutManager?.onRestoreInstanceState(state)
            }else{
                itemView.recyclerItems.layoutManager?.scrollToPosition(0)
            }
        }

        override fun getScrollStateKey(): String? {
            val stateKey = currentList.get(adapterPosition).id.toString()
            d{"key = $stateKey"}
            return stateKey
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Category)
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is CategoriesViewHolder -> {
                holder.onRecycled()
            }
        }
        val key = currentList.get(holder.layoutPosition).id
        if(key == null)return
        scrollStates[key] =
            holder.itemView.findViewById<RecyclerView>(R.id.recyclerItems).layoutManager?.onSaveInstanceState()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

    }
}