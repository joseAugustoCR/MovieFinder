package com.example.app.ui.main.community.post_detail
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.app.api.PostComment
import com.example.app.utils.extensions.load
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.view_comment_parent.view.*


class CommentsAdapter(private val interaction: Interaction? = null, var list:ArrayList<PostComment?> = ArrayList()) :
        androidx.recyclerview.widget.RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {


    companion object{
        val TYPE_ITEM = 0
        val TYPE_LOADING = 1
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(
                    R.layout.view_comment_parent,
                    parent,
                    false
            )
            return CommentsViewHolder(view, interaction, inflater)
    }


    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bindItems(list[position])
    }



    fun updateItem(newItem: PostComment, notify:Boolean){
        list?.forEachIndexed { index, oldItem ->
            if(newItem.id == oldItem?.id){
                list.set(index, newItem)
                if(notify){
                    notifyItemChanged(index)
                }
            }
        }
    }



    fun updateList(plist:ArrayList<PostComment?>?){
        if(plist == null) return
        list = plist;
        notifyDataSetChanged()
    }

    fun addItems(plist:ArrayList<PostComment?>?){
        if(plist == null) return
        plist?.forEachIndexed { index, campaign ->
            list.add(campaign)
            notifyItemInserted(list.size -1)
            d{"add item"}
        }
    }


    fun clearList(){
        list.clear();
        notifyDataSetChanged()
    }

    inner class CommentsViewHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?=null,
            val inflater: LayoutInflater
    ) : RecyclerView.ViewHolder(itemView) {




        fun bindItems(item: PostComment?) {
            item?:return
//            val inflater = itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            val commentUser = item?.appuser
            itemView.commentUserAvatar.load(commentUser?.image.toString(), isRoundImage = true, placeholder = R.drawable.ic_star)
            itemView.userName.text = commentUser?.name
            if(item.appuser?.verified == true){
                itemView.userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(itemView.context, R.drawable.ic_star), null)
            }else{
                itemView.userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        null, null)
            }
            itemView.comment.text = item.comment
            itemView.timeAgo.text = item.ago
            itemView.replyBtn.setOnClickListener {
                interaction?.onReply(adapterPosition, item)
            }
            itemView.repliesLayout.removeAllViews()

            if(item.reply.isNullOrEmpty()){
                itemView.repliesLayout.visibility = View.GONE
            }else{
                itemView.repliesLayout.visibility = View.VISIBLE

                item.reply?.forEachIndexed { index, postComment ->
                    val childView = inflater.inflate(R.layout.view_comment_child, itemView.repliesLayout)
                    val user = postComment?.appuser
                    itemView.repliesLayout.getChildAt(index).commentUserAvatar.load(user?.image.toString(), isRoundImage = true, placeholder = R.drawable.ic_star)
                    itemView.repliesLayout.getChildAt(index).userName.text = user?.name
                    if(user?.verified == true){
                        itemView.repliesLayout.getChildAt(index).userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                ContextCompat.getDrawable(itemView.context, R.drawable.ic_star), null)
                    }else{
                        itemView.repliesLayout.getChildAt(index).userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                null, null)
                    }
                    itemView.repliesLayout.getChildAt(index).timeAgo.text = postComment.ago
                    itemView.repliesLayout.getChildAt(index).comment.text = postComment.comment
                }
            }

        }


    }

    interface Interaction {
        fun onItemSelected(position: Int, item: PostComment)
        fun onReply(position: Int, item: PostComment)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}