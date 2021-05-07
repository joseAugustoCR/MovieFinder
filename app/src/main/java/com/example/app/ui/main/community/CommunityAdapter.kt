package com.example.app.ui.main.community
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.example.app.R
import com.example.app.SessionManager
import com.example.app.api.Post
import com.example.app.utils.extensions.load
import com.example.app.utils.extensions.loadDrawable
import com.github.ajalt.timberkt.d
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.view_community_post.view.*
import org.jetbrains.anko.startActivity


class CommunityAdapter(private val interaction: Interaction? = null, var lifecycle:Lifecycle, var list:ArrayList<Post?> = ArrayList(),
var sessionManager: SessionManager) :
        androidx.recyclerview.widget.RecyclerView.Adapter<CommunityAdapter.TimelineViewHolder>() {


    companion object{
        val TYPE_ITEM = 0
        val TYPE_LOADING = 1
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {


        if(viewType == TYPE_ITEM){
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.view_community_post,
                    parent,
                    false
            )
            val youtubePlayerView = view.findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
            lifecycle.addObserver(youtubePlayerView)
            return TimelineViewHolder(
                    view,
                    interaction,
                    youtubePlayerView
            )
        }else{
            val view = LayoutInflater.from(parent.context).inflate(
                    R.layout.view_community_loading,
                    parent,
                    false
            )
            return TimelineViewHolder(view)
        }

    }
    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bindItems(list[position])
    }



    fun updateItem(newItem:Post, notify:Boolean){
        list?.forEachIndexed { index, oldItem ->
            if(newItem.id == oldItem?.id){
                list.set(index, newItem)
                if(notify){
                    notifyItemChanged(index)
                }
            }
        }
    }

    fun deleteItem(newItem:Post){
        var itemToDeletePos = -1
        list?.forEachIndexed { index, oldItem ->
            if(newItem.id == oldItem?.id){
               itemToDeletePos = index
                return@forEachIndexed
            }
        }
        if(itemToDeletePos > -1){
            list.removeAt(itemToDeletePos)
            notifyItemRemoved(itemToDeletePos)
        }
    }

    fun incrementLike(item:Post, numberToAdd:Int):Int {
        item.likes_count = (item.likes_count ?: 0) + numberToAdd
        item.has_like = if(numberToAdd == 1) true else false
        updateItem(item, false)
        return item.likes_count?:0
    }

    fun updateList(plist:ArrayList<Post?>){
        if(plist == null) return
        list = plist;
        notifyDataSetChanged()
    }

    fun addItems(plist:ArrayList<Post?>?){
        if(plist == null) return
        plist?.forEachIndexed { index, campaign ->
            list.add(campaign)
            notifyItemInserted(list.size -1)
            d{"add item"}
        }
    }

    fun addLoading(){
        try {
            removeLoading()
            list.add(Post(id = -1, type = Post.TYPE_LOADING))
            notifyItemInserted(list.size -1)
            d{"add LOADING"}
        } catch (e: Exception) {
        }

    }

    fun removeLoading(){
        try {
            for (i in list.size-1 downTo 0){
                if(list.get(i)?.type.equals(Post.TYPE_LOADING, true) == true){
                    list.removeAt(i)
                    notifyItemRemoved(i)
                    d{"remove LOADING"}

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            d{"remove LOADING ERROR"}

        }
    }

    fun clearList(){
        list.clear();
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if(list.get(position)?.type.equals(Post.TYPE_LOADING) == true){
            return TYPE_LOADING
        }else{
            return TYPE_ITEM
        }
    }

    inner class TimelineViewHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?=null,
            val youTubePlayerView: YouTubePlayerView?=null
    ) : RecyclerView.ViewHolder(itemView) {

        var youtubePlayer:YouTubePlayer?=null
        var currentVideoID:String? = null


        init {
            youTubePlayerView?.getPlayerUiController()?.showVideoTitle(false)
            youTubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                    youtubePlayer = initializedYouTubePlayer
                    d{"on ready $adapterPosition"}

                    if(currentVideoID.isNullOrEmpty() == false){
                        d{"on ready cue $adapterPosition"}
                        youtubePlayer?.cueVideo( currentVideoID.toString(), 0f)
                    }
                }

                override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                    super.onVideoLoadedFraction(youTubePlayer, loadedFraction)
                    d{"$adapterPosition - loaded: $loadedFraction"}
                }

                override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                    super.onError(youTubePlayer, error)
                    d{"$adapterPosition error - ${error.name}"}
                }
            })

        }

        fun bindItems(item: Post?) {
            if(itemViewType == TYPE_LOADING){
                return
            }
            if(item == null) return

            interaction?.view(adapterPosition, item)
            setListeners(item)


            item.appuser?.let {
                itemView.userName.text = it.name
                itemView.userAvatar.load(it.image.toString(), placeholder = R.drawable.ic_star, isRoundImage = true)
            }
            itemView.timeAgo.text = item.ago

            if(item.last_comment == null){
//                itemView.commentsBtn.text = "Faça um comentário"
                itemView.lastCommentLayout.visibility = View.GONE

            }else{
                val lastCommentUser = item.last_comment?.appuser
//                itemView.commentsBtn.text = "Ver comentários"
                itemView.lastCommentLayout.visibility = View.VISIBLE
                itemView.commentUserAvatar.load(lastCommentUser?.image.toString(), placeholder = R.drawable.ic_star, isRoundImage = true)
                itemView.lastComment.text = item.last_comment?.comment
            }

            if(item.appuser?.verified == true){
                itemView.userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(itemView.context, R.drawable.ic_star), null)
            }else{
                itemView.userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        null, null)
            }

            if(item.subscribe == true){
                itemView.exclusiveTxt.visibility = View.VISIBLE
//                if(UserPref.activeUser?.subscribed != true && (item.type == Post.TYPE_FILE || item.type == Post.TYPE_YOUTUBE)) {
//                    itemView.maskExclusive.visibility = View.VISIBLE
//                }else{
//                    itemView.maskExclusive.visibility = View.GONE
//                }
            }else{
                itemView.exclusiveTxt.visibility = View.GONE
                itemView.maskExclusive.visibility = View.GONE
            }

            itemView.viewsCount.text = formatCount(item.views_count?:0)
            setLikeBtn(item)
            setCommentBtn(item)

            if(item.desc.isNullOrEmpty()){
                itemView.description.visibility =View.GONE
            }else{
                itemView.description.visibility =View.VISIBLE
                itemView.description.text = item.desc
            }

            when(item?.type){
                Post.TYPE_TEXT->{
                    itemView.postImage.visibility = View.GONE
                    itemView.youtubePlayerView.visibility = View.GONE
                    itemView.mediaLayout.visibility = View.GONE

                }

                Post.TYPE_IMAGE->{
                    itemView.postImage.visibility = View.VISIBLE
                    itemView.postImage.scaleType = ImageView.ScaleType.FIT_CENTER
                    itemView.postImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        dimensionRatio = "1:1"
                    }
                    itemView.youtubePlayerView.visibility = View.GONE
                    itemView.postImage.post {
                        itemView.postImage.load(item.image.toString(), scaleType = ImageView.ScaleType.FIT_CENTER, placeholder = R.drawable.ic_movie)
                    }
                    itemView.mediaLayout.visibility = View.VISIBLE

                }

                Post.TYPE_YOUTUBE->{
                    itemView.postImage.visibility = View.GONE
                    itemView.youtubePlayerView.visibility = View.VISIBLE
                    itemView.mediaLayout.visibility = View.VISIBLE
                    cueVideo(item.id_youtube)
                }

                Post.TYPE_FILE->{
                    itemView.postImage.visibility = View.VISIBLE
                    itemView.postImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    itemView.postImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        dimensionRatio = "16:9"
                    }
                    itemView.youtubePlayerView.visibility = View.GONE
                    itemView.mediaLayout.visibility = View.VISIBLE
                    itemView.postImage.post {
                        itemView.postImage.loadDrawable(R.drawable.ic_star)
                    }

                }
            }

        }

        fun setLikeBtn(item: Post){
            itemView.likesCount.text = formatCount(item.likes_count?:0)
            if(item.has_like == true){
                itemView.likeIcon.setImageResource(R.drawable.ic_star)
            }else{
                itemView.likeIcon.setImageResource(R.drawable.ic_star)
            }
        }

        fun setCommentBtn(item:Post){
            itemView.commentsCount.text = formatCount(item.comments_count?:0)
            if(item.has_comment == true){
                itemView.commentsIcon.setImageResource(R.drawable.ic_star)
            }else{
                itemView.commentsIcon.setImageResource(R.drawable.ic_star)
            }
        }

        fun formatCount(count:Int):String{
            var countValue = count.toFloat()
            if(count>=1000){
                countValue = count/1000f
                return String.format("%.1f", countValue) + "K"
            }
            return countValue.toInt().toString()

        }

        fun setListeners(item: Post?){
            item ?: return
            itemView.setOnClickListener {
                    interaction?.onItemSelected(adapterPosition, item)
            }

            if(item.call_to_action_title.isNullOrEmpty() || item.call_to_action_link.isNullOrEmpty()){
                itemView.actionBtn.visibility =View.GONE
                itemView.actionBtn.text = "Ver"
            }else{
                itemView.actionBtn.visibility =View.VISIBLE
                itemView.actionBtn.text = item.call_to_action_title
            }
            itemView.actionBtn.setOnClickListener {
                if(item.subscribe == true && sessionManager.isSubscriber() != true){
                   interaction?.onBuy(adapterPosition, item)
                    return@setOnClickListener
                }
                interaction?.action(adapterPosition, item)
            }

            itemView.optionsMenuBtn.setOnClickListener {
                val popup = PopupMenu(itemView.context, itemView.optionsMenuBtn)
                popup.inflate(R.menu.post_options)
                popup.menu.getItem(1).setVisible(item.appuser_id == sessionManager.getUserID())
                popup.menu.getItem(0).setVisible(item.appuser_id != sessionManager.getUserID())
                popup.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.report->{
                            interaction?.onReport(adapterPosition, item)
                            return@setOnMenuItemClickListener true
                        }
                        R.id.delete->{
                            interaction?.onDelete(adapterPosition, item)
                            return@setOnMenuItemClickListener true
                        }
                        else -> return@setOnMenuItemClickListener false
                    }
                }
                popup.show()
            }


            itemView.shareBtn.setOnClickListener {
                interaction?.share(adapterPosition, item)
            }

            itemView.likesLayout.setOnClickListener {
                if(item.has_like == true){
                    interaction?.unlike(adapterPosition, item)
                    var newCount = incrementLike(item, -1)
                    itemView.likesCount.text = formatCount(newCount)
                    itemView.likeIcon.setImageResource(R.drawable.ic_star)
                }else{
                    interaction?.like(adapterPosition, item)
                    var newCount = incrementLike(item, 1)
                    itemView.likesCount.text = formatCount(newCount)
                    itemView.likeIcon.setImageResource(R.drawable.ic_star)
                }
            }

//            itemView.commentsBtn.setOnClickListener {
//                interaction?.onItemSelected(adapterPosition, item, true)
//            }

            itemView.commentsLayout.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item, true)
            }

            itemView.postImage.setOnClickListener {
                if(item.type.equals(Post.TYPE_FILE) == false){
                    interaction?.onItemSelected(adapterPosition, item)
                }else{
                    if(item.subscribe == true && sessionManager.isSubscriber() != true){
                        interaction?.onBuy(adapterPosition, item)

                        return@setOnClickListener
                    }
                    interaction?.openFile(adapterPosition, item)
                }
            }

            itemView.description.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            itemView.maskExclusive.setOnClickListener {
                interaction?.onBuy(adapterPosition, item)
            }
        }

        fun cueVideo(videoID:String?){
            if(videoID.isNullOrEmpty() == true){
                return
            }
            currentVideoID = videoID

            if(youtubePlayer == null){
                return
            }
            d{"$adapterPosition - bind cue"}
            youtubePlayer?.cueVideo(videoId = videoID.toString(), startSeconds = 0f)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Post, isComment:Boolean=false)
        fun like(position: Int, item: Post)
        fun unlike(position: Int, item: Post)
        fun view(position: Int, item: Post)
        fun share(position: Int, item: Post)
        fun action(position: Int, item: Post)
        fun openFile(position: Int, item: Post)
        fun onReport(position: Int, item: Post)
        fun onDelete(position: Int, item: Post)
        fun onBuy(position: Int, item: Post)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}