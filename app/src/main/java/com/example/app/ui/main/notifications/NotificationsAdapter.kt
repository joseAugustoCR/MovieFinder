package com.example.app.ui.main.notifications

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.app.R
import com.example.app.api.Notification
import kotlinx.android.synthetic.main.view_notification.view.*

class NotificationsAdapter(private val interaction: Interaction? = null) :
        ListAdapter<Notification, RecyclerView.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Notification>() {

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NotificationsViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.view_notification,
                        parent,
                        false
                ),
                interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotificationsViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    class NotificationsViewHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Notification) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            title.text = item.title
            message.text = item.message
            arrow.visibility = if(item.url.isNullOrEmpty()) View.GONE else View.VISIBLE
            if(item.read_at == null){
                itemView.icon.setImageResource(R.drawable.ic_star)
                itemView.title.setTextColor(ContextCompat.getColor(context, R.color.white))
                itemView.message.setTextColor(ContextCompat.getColor(context, R.color.white))
            }else{
                itemView.icon.setImageResource(R.drawable.ic_star)
                itemView.title.setTextColor(ContextCompat.getColor(context, R.color.colorNotificationDisabled))
                itemView.message.setTextColor(ContextCompat.getColor(context, R.color.colorNotificationDisabled))

            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Notification)
    }
}