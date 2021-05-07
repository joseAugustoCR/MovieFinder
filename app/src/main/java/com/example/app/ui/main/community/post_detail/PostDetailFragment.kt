package com.example.app.ui.main.community.post_detail

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.api.Post
import com.example.app.api.PostComment
import com.example.app.api.Resource
import com.example.app.base.BaseFragment
import com.example.app.base.NAVIGATION_RESULT_CANCELED
import com.example.app.base.NAVIGATION_RESULT_OK
import com.example.app.base.REQUEST_POST_DETAIL
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.ui.MainActivity
import com.example.app.ui.main.community.CommunityFragment
import com.example.app.ui.main.community.CommunityViewModel
import com.example.app.utils.extensions.*
import com.example.app.utils.navigation.NavigationResult
import com.github.ajalt.timberkt.d
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import kotlinx.android.synthetic.main.post_detail_fragment.*
import kotlinx.android.synthetic.main.post_detail_fragment.loadingLayout
import javax.inject.Inject

class PostDetailFragment : BaseFragment(), CommentsAdapter.Interaction {
    var post: Post?=null
    var youtubePlayer: YouTubePlayer?=null
    lateinit var commentsAdapter:CommentsAdapter
    var replyComment: MutableLiveData<PostComment?> = MutableLiveData(null)
    val args:PostDetailFragmentArgs by navArgs()
    var currentVideoID:String? = null
    val handler: Handler = Handler()
    var isUpdatingComments:Boolean = false
    var commentID:Int=-1
    @Inject
    lateinit var savedStateFactory: InjectingSavedStateViewModelFactory


    companion object {
        fun newInstance() = PostDetailFragment()
    }

    private lateinit var viewModel: PostDetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.post_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[PostDetailViewModel::class.java]
        commentID = args.commentId
        post = args.post
        setObservers()
        setListeners(post)
        initYoutubePlayer()
        initCommentsRecycler()
        viewModel.getPostDetail(post?.id ?: 0)
        viewModel.view(post?.id ?: 0)
    }

    fun initCommentsRecycler(){
        commentsAdapter = CommentsAdapter(this, ArrayList())
        val linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        commentsRecycler.layoutManager = linearLayoutManager
        commentsRecycler.adapter = commentsAdapter
    }

    fun initYoutubePlayer(){
        lifecycle.addObserver(youtubePlayerView)
        youtubePlayerView?.getPlayerUiController()?.showVideoTitle(false)
        youtubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                youtubePlayer = initializedYouTubePlayer

                if(currentVideoID.isNullOrEmpty() == false){
                    if(post?.subscribe == true && sessionManager.isSubscriber() != true){
                        youtubePlayer?.cueVideo( videoId = currentVideoID.toString(), startSeconds = 0f)
                    }else{
                        youtubePlayer?.loadOrCueVideo(lifecycle, videoId = currentVideoID.toString(), startSeconds = 0f)
                    }
                }
            }

            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
                super.onVideoLoadedFraction(youTubePlayer, loadedFraction)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
            }


        })

        youtubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                exitFullScreen()

            }

        })
    }

    fun enterFullScreen(){
        for (view in postConstraint.children) {
            if(view.id != R.id.mediaLayout) {
                view.visibility = View.GONE
                view.invalidate()
            }
        }

        for (view in mainLinearLayout.children) {
            if(view.id != R.id.postCard) {
                view.visibility = View.GONE
                view.invalidate()
            }
        }

//        val  layoutParam = postCard.layoutParams
        val layoutParam = postCard.layoutParams as LinearLayout.LayoutParams
        layoutParam.height = LinearLayout.LayoutParams.MATCH_PARENT
        postCard.layoutParams = layoutParam

        val layoutParamConstraint = postConstraint.layoutParams as FrameLayout.LayoutParams
        layoutParamConstraint.height = LinearLayout.LayoutParams.MATCH_PARENT
        postConstraint.layoutParams = layoutParamConstraint

        val layoutParamMedia = mediaLayout.layoutParams as ConstraintLayout.LayoutParams
        layoutParamMedia.height = LinearLayout.LayoutParams.MATCH_PARENT
        layoutParamMedia.topMargin = 0
        mediaLayout.layoutParams = layoutParamMedia

        val layoutParamVideo = youtubePlayerView.layoutParams as ConstraintLayout.LayoutParams
        layoutParamVideo.height = LinearLayout.LayoutParams.MATCH_PARENT
        youtubePlayerView.layoutParams = layoutParamVideo
    }

    fun exitFullScreen(){
        for (view in postConstraint.children) {
            if(view.id != R.id.mediaLayout) {
                view.visibility = View.VISIBLE
                view.invalidate()
            }
        }

        for (view in mainLinearLayout.children) {
            if(view.id != R.id.postCard) {
                view.visibility = View.VISIBLE
                view.invalidate()
            }
        }

        val layoutParam = postCard.layoutParams as LinearLayout.LayoutParams
        layoutParam.height = LinearLayout.LayoutParams.WRAP_CONTENT
        postCard.layoutParams = layoutParam

        val layoutParamConstraint = postConstraint.layoutParams as FrameLayout.LayoutParams
        layoutParamConstraint.height = LinearLayout.LayoutParams.WRAP_CONTENT
        postConstraint.layoutParams = layoutParamConstraint

        val layoutParamMedia = mediaLayout.layoutParams as ConstraintLayout.LayoutParams
        layoutParamMedia.height = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutParamMedia.topMargin = 0.px()
        mediaLayout.layoutParams = layoutParamMedia

        val layoutParamVideo = youtubePlayerView.layoutParams as ConstraintLayout.LayoutParams
        layoutParamVideo.height = LinearLayout.LayoutParams.WRAP_CONTENT
        youtubePlayerView.layoutParams = layoutParamVideo
    }

    fun setListeners(item: Post?){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                setNavigationResult(
                    NavigationResult(
                        REQUEST_POST_DETAIL,
                        resultCode = NAVIGATION_RESULT_OK,
                        bundleOf(
                            CommunityFragment.KEY_POST to post,
                        )
                    )
                )
                navController.popBackStack()

            }
        })

        item ?: return

        maskExclusive.setOnClickListener {
            onBuy()
        }

        optionsMenuBtn.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.inflate(R.menu.post_options)
            popup.menu.getItem(1).setVisible(item.appuser_id == sessionManager.getUserID())
            popup.menu.getItem(0).setVisible(item.appuser_id != sessionManager.getUserID())
            popup.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.report->{
                        onReport(item)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete->{
                        onDelete(item)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            popup.show()
        }

        input.inputEditText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
//                scrollToBottom()
            }
        }
        input.inputEditText.setOnClickListener {
//            scrollToBottom()
        }

        if(item.call_to_action_title.isNullOrEmpty() || item.call_to_action_link.isNullOrEmpty()){
            actionBtn.visibility =View.INVISIBLE
        }else{
            actionBtn.visibility =View.VISIBLE
            actionBtn.text = item.call_to_action_title
        }
        actionBtn.setOnClickListener {
            if(item.call_to_action_title.isNullOrEmpty() || item.call_to_action_link.isNullOrEmpty()) {
                return@setOnClickListener
            }
            action(item)
        }

//        backBtn.setOnClickListener {
//            onBackPressed()
//        }

        closeReply.setOnClickListener {
            replyComment.value = null
        }

        likesLayout.setOnClickListener {
            if(item.has_like == true){
                unlike(item)
                var newCount = incrementLike(-1)
                likesCount.text = formatCount(newCount)
                likeIcon.setImageResource(R.drawable.ic_star)
            }else{
                like(item)
                var newCount = incrementLike(1)
                likesCount.text = formatCount(newCount)
                likeIcon.setImageResource(R.drawable.ic_star)
            }
        }

        postImage.setOnClickListener {
            if(item.type.equals(Post.TYPE_FILE) == false){
                return@setOnClickListener
            }
            openFile(item)
        }

        writeCommentBtn.setOnClickListener {
            nested.post(Runnable {
                nested.fullScroll(View.FOCUS_DOWN)
                handler.postDelayed({
                    input.inputEditText.performClick()
                    input.inputEditText.requestFocus()
                    requireActivity().showKeyboard(input.inputEditText)
                },600)
            })
        }

        shareBtn.setOnClickListener {
            share()
        }

        input.setInputListener {
            if(it.toString().trim().isEmpty()){
                progress_top.snack("Escreva um comentário", R.color.colorSnackError, {})
                input.inputEditText.setText("")
                input.button.isEnabled = false
                return@setInputListener false
            }
            requireActivity().hideKeyboard()
            input.inputEditText.setText("")
            input.button.isEnabled = false
            val request = PostComment(
                comment = it.toString(),
                comment_reply_id = replyComment.value?.id
            )
            viewModel.comment(request)
            return@setInputListener true
        }
    }


    fun setObservers(){
        viewModel.observLike().observe(viewLifecycleOwner, Observer {
        })
        viewModel.observUnlike().observe(viewLifecycleOwner, Observer {
        })
        viewModel.observeShareCount().observe(viewLifecycleOwner, Observer {
        })
        viewModel.observeReport().observe(viewLifecycleOwner, Observer {
        })
        viewModel.observeDelete().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                setNavigationResult(
                    NavigationResult(
                        REQUEST_POST_DETAIL,
                        resultCode = NAVIGATION_RESULT_OK,
                        bundleOf(
                            CommunityFragment.KEY_POST to post,
                            CommunityFragment.KEY_TYPE to "delete"
                        )
                    )
                )
                navController.popBackStack()

            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                loadingLayout.snack("Ops, algo deu errado. Tente novamente.", R.color.colorSnackError, {})
            }
        })


        viewModel.observeDetail().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                loadingLayout.visibility = View.GONE
                post = it.data
                post?.let {
                    setData(it)
                }



            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                loadingLayout.snack("Ops, algo deu errado. Tente novamente.", R.color.colorSnackError, {})
            }
        })


        viewModel.observeComment().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                isUpdatingComments = true
                viewModel.getPostDetail(post?.id ?: 0)

            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                loadingLayout.snack("Ops, algo deu errado. Tente novamente.", R.color.colorSnackError, {})
            }
        })

        replyComment.observe(this, Observer {
            if(it == null){
                replyLayout.visibility = View.GONE
            }else{
                replyLayout.visibility = View.VISIBLE
                replyTxt.text = "Respondendo ${it.appuser?.name}"
            }
        })

    }

    fun setData(postData: Post){
        post = postData
        post?:return
        setListeners(post)
        setCommentBtn(postData)

        //if is a request only for update the comments (after a new comment send)
        if(isUpdatingComments){
            isUpdatingComments = false
            setComments()
            if(replyComment.value == null){
                handler.postDelayed({
                    nested.post(Runnable {
                        nested.fullScroll(View.FOCUS_DOWN)
                    })
                }, 500)

            }else{
                var replyPos = 0
                commentsAdapter?.list?.forEachIndexed { index, postComment ->
                    if(postComment?.id == replyComment.value?.id){
                        replyPos = index
                    }
                }
                var windowPos = IntArray(2)
                commentsRecycler.getChildAt(replyPos).getLocationInWindow(windowPos)
                var viewHeight = commentsRecycler.getChildAt(replyPos).height
                val displayMetrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenHeight: Int = displayMetrics.heightPixels
                d{"window pos = ${windowPos.get(0)} - ${ windowPos.get(1)}"}
                handler.postDelayed({
                    nested.smoothScrollBy(0, windowPos.get(1)+viewHeight - (screenHeight.div(2)))
                }, 500)
            }
            replyComment.value = null
            return
        }

        if(postData.subscribe == true){
            exclusiveTxt.visibility = View.VISIBLE
            if(sessionManager.isSubscriber() != true && (postData.type == Post.TYPE_FILE || postData.type == Post.TYPE_YOUTUBE)) {
                maskExclusive.visibility = View.VISIBLE
            }else{
                maskExclusive.visibility = View.GONE
            }
        }else{
            exclusiveTxt.visibility = View.GONE
            maskExclusive.visibility = View.GONE
        }


        if(postData.appuser?.verified == true){
            userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_star), null)
        }else{
            userName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                null, null)
        }

        userName.text = post?.appuser?.name
        userAvatar.load(post?.appuser?.image.toString(), isRoundImage =  true, placeholder = R.drawable.ic_star)
        timeAgo.text = post?.ago.toString()
        viewsCount.text = formatCount(post?.views_count?:0)
        setLikeBtn(postData)
        if(post?.desc.isNullOrEmpty()){
            description.visibility = View.GONE
        }else{
            description.visibility = View.VISIBLE
            description.text = post?.desc
        }

        if(post?.type.equals(Post.TYPE_YOUTUBE) == true){
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }else{
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        when(post?.type){
            Post.TYPE_TEXT->{
                postImage.visibility = View.GONE
                youtubePlayerView.visibility = View.GONE
                mediaLayout.visibility = View.GONE

            }

            Post.TYPE_IMAGE->{
                postImage.visibility = View.VISIBLE
                postImage.scaleType = ImageView.ScaleType.FIT_CENTER
                postImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    dimensionRatio = "1:1"
                }
                youtubePlayerView.visibility = View.GONE
                postImage.load(post?.image.toString(), scaleType = ImageView.ScaleType.FIT_CENTER, placeholder = R.drawable.ic_star)
                mediaLayout.visibility = View.VISIBLE

            }

            Post.TYPE_YOUTUBE->{
                postImage.visibility = View.GONE
                youtubePlayerView.visibility = View.VISIBLE
                mediaLayout.visibility = View.VISIBLE
                cueVideo(post?.id_youtube)
            }

            Post.TYPE_FILE->{
                postImage.visibility = View.VISIBLE
                postImage.scaleType = ImageView.ScaleType.CENTER_CROP
                postImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    dimensionRatio = "16:9"
                }
                youtubePlayerView.visibility = View.GONE
                mediaLayout.visibility = View.VISIBLE
                postImage.loadDrawable(R.drawable.ic_star)

            }
        }
        setComments()

    }


    fun cueVideo(videoID:String?){
        if(videoID.isNullOrEmpty() == true){
            return
        }
        currentVideoID = videoID

        if(youtubePlayer == null){
            return
        }
        if(post?.subscribe == true && sessionManager.isSubscriber() != true){
            youtubePlayer?.cueVideo( videoId = videoID.toString(), startSeconds = 0f)
        }else{
            youtubePlayer?.loadOrCueVideo(lifecycle, videoId = videoID.toString(), startSeconds = 0f)
        }
    }


    fun setLikeBtn(item: Post){
        likesCount.text = formatCount(item.likes_count?:0)
        if(item.has_like == true){
            likeIcon.setImageResource(R.drawable.ic_star)
        }else{
            likeIcon.setImageResource(R.drawable.ic_star)
        }
    }

    fun setCommentBtn(item:Post){
        var commentsCountValue:Int = item.comments_count?:0
        if(isUpdatingComments){
            commentsCountValue++;
            post?.comments_count = (post?.comments_count?:0)+1
        }
        if(commentsCountValue == 1){
            commentsCount.text = "1 comentário"
        }else{
            commentsCount.text = formatCount(commentsCountValue?:0) + " comentários"
        }
        if(item.has_comment == true){
            commentsIcon.setImageResource(R.drawable.ic_star)
        }else{
            commentsIcon.setImageResource(R.drawable.ic_star)
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

    override fun onDestroy() {
        youtubePlayerView?.release()
        super.onDestroy()
    }

    fun setComments(){
        if(post?.comments.isNullOrEmpty()){
            commentsRecycler.visibility = View.GONE
        }else{
            commentsRecycler.visibility = View.VISIBLE
        }
        if(commentID > 0){
            d{"comment ID = $commentID"}
            var replyPos = 0
            val list = post?.comments ?: arrayListOf()
            val temp = ArrayList<PostComment?>()
            temp.addAll(list)

            list?.forEachIndexed { index, postComment ->
                if(postComment?.id == commentID){
                    replyPos = index
                    val comment = postComment
                    temp.removeAt(replyPos)
                    temp.add(0, postComment)
                    return@forEachIndexed
                }
            }
            commentID = -1
            commentsAdapter?.updateList(temp)

        }else{
            commentsAdapter?.updateList(post?.comments)
        }

    }

    fun incrementLike(numberToAdd:Int):Int {
        post?.likes_count = (post?.likes_count ?: 0) + numberToAdd
        post?.has_like = if(numberToAdd == 1) true else false
        return post?.likes_count?:0
    }


    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
        super.onStop()

    }

    // Actions
    fun onBuy(){

    }


    fun like(item: Post) {
        item.id?.let {
            viewModel.like(it)
        }
    }

    fun unlike(item: Post) {
        item.id?.let {
            viewModel.unlike(it)
        }
    }

    fun view(item: Post) {
        item.id?.let {
            viewModel.view(item.id ?: 0)
        }
    }

    fun share() {
        viewModel.shareCount(post?.id ?: 0)
        var url = post?.url_share
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + url);
        try {
            startActivity(Intent.createChooser(shareIntent,"Compartilhar"))
        } catch (e: Exception) {
            e.printStackTrace()
            nested.snack("Nenhum aplicativo para compartilhar instalado", R.color.colorSnackError, {})
        }
    }

    fun action(item: Post) {
        if(item.subscribe == true && sessionManager.isSubscriber() != true){
            onBuy()
            return
        }
        if(item.call_to_action_link.isNullOrEmpty() == false){
            item.call_to_action_link.toString().loadOnExternalBrowser(requireActivity())
        }
    }

    fun openFile(item: Post) {
        if(item.subscribe == true && sessionManager.isSubscriber() != true){
            onBuy()
            return
        }
        if(item.file.isNullOrEmpty() == false){
            var fileUrl = item.file.toString()
            fileUrl.loadOnExternalBrowser(requireActivity())
        }
    }

    fun onDelete(item:Post){
        MaterialAlertDialogBuilder(requireContext()).setTitle("Tem certeza que deseja remover essa publicação?")
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->
                viewModel.delete(item.id ?: 0)
            })
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, which ->

            })
            .show()
    }

    fun onReport(item:Post){
        MaterialAlertDialogBuilder(requireContext()).setTitle("Tem certeza que deseja denunciar essa publicação?")
            .setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, which ->
                viewModel.report(item.id ?: 0)
                input.snack("Publicação denunciada. Nossa equipe irá analisar esse conteúdo.", R.color.colorAccent, {})
            })
            .setNegativeButton("Não", DialogInterface.OnClickListener { dialog, which ->

            })
            .show()
    }

    override fun onItemSelected(position: Int, item: PostComment) {

    }

    override fun onReply(position: Int, item: PostComment) {
        replyComment.value = item
        writeCommentBtn.performClick()
    }


}