package com.example.app.ui.main.community

import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app.R
import com.example.app.api.Post
import com.example.app.api.PostsViewRequest
import com.example.app.api.Resource
import com.example.app.base.BaseFragment
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.utils.Constants.Companion.PAGINATION_SIZE
import com.example.app.utils.VIEWED_POSTS
import com.example.app.utils.extensions.loadOnExternalBrowser
import com.example.app.utils.extensions.snack
import com.github.ajalt.timberkt.d
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.collapsing_appbar_regular.*
import kotlinx.android.synthetic.main.community_fragment.*
import javax.inject.Inject

class CommunityFragment : BaseFragment(), CommunityAdapter.Interaction {

    @Inject
    lateinit var savedStateFactory: InjectingSavedStateViewModelFactory
    var scrollListener: RecyclerView.OnScrollListener? = null
    var canLoadMore:Boolean = true

    companion object {
        fun newInstance() = CommunityFragment()
    }
    lateinit var adapter: CommunityAdapter
    private lateinit var viewModel: CommunityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.community_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[CommunityViewModel::class.java]
        setupToolbar()
        setScrollListener()
        setListeners()
        adapter = CommunityAdapter(this, lifecycle, sessionManager = sessionManager)
        setRecycler()
        setObservers()
        viewModel.getPosts(0)
    }

    override fun onResume() {
        super.onResume()
        if (scrollListener != null) {
            recycler.addOnScrollListener(scrollListener!!)
        }
    }

    override fun onStop() {
        if (scrollListener != null) {
            recycler.removeOnScrollListener(scrollListener!!)
        }
        super.onStop()
    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        collapsingToolbar.title = "Comunidade"
        setHasOptionsMenu(true)
    }

    fun setScrollListener(){
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstItem = layoutManager.findFirstVisibleItemPosition()
                val lastVIsiblePosition = layoutManager.findLastVisibleItemPosition()
                val itemCount = adapter.itemCount

                if((lastVIsiblePosition+1) >= itemCount-3 && canGetMoreItems() ){
                    viewModel.loadMore(itemCount)
                    d{"getting more size ${adapter.itemCount}"}
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        }
    }

    fun canGetMoreItems() : Boolean{
        return canLoadMore
    }

    fun refresh(){
        adapter?.clearList()
        viewModel.getPosts(0)
    }

    fun setListeners(){
        swipe.setOnRefreshListener {
            refresh()
        }
    }

    fun setRecycler(){
        recycler.adapter = adapter
    }

    fun setData(posts:ArrayList<Post?>?){
        if(posts?.size != null && posts.size >= PAGINATION_SIZE){
            canLoadMore = true
        }else{
            canLoadMore = false
        }
        if(adapter.itemCount == 0 && posts.isNullOrEmpty()){
            emptyLayout.visibility = View.VISIBLE
            return
        }
        emptyLayout.visibility = View.GONE
        d{"adding data"}
        adapter.addItems(posts)
    }

    fun setObservers(){
        viewModel.observePosts().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                swipe.isRefreshing = false
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.GONE
                setData(it.data)

            } else if (it.status == Resource.Status.LOADING) {
                canLoadMore = false
                if(adapter?.itemCount == 0) {
                    loadingLayout.visibility = View.VISIBLE
                }else{
                    swipe.isRefreshing = true
                }
                errorLayout.visibility = View.GONE
                emptyLayout.visibility = View.GONE

            } else if (it.status == Resource.Status.ERROR) {
                swipe.isRefreshing = false
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE
            }
        })

        viewModel.observeLoadMore().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                recycler.post(Runnable {
                    adapter?.removeLoading()
                })
                setData(it.data)

            } else if (it.status == Resource.Status.LOADING) {
                recycler.post(Runnable {
                    adapter?.addLoading()
                })
                canLoadMore = false

            } else if (it.status == Resource.Status.ERROR) {
                recycler.post(Runnable {
                    adapter?.removeLoading()
                })

            }
        })

        viewModel.observeDelete().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                refresh()

            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                recycler.snack("Ops, algo deu errado. Tente novamente.", R.color.colorSnackError, {})
            }
        })

        viewModel.observLike().observe(viewLifecycleOwner, Observer {
        })
        viewModel.observUnlike().observe(viewLifecycleOwner, Observer {
        })
        viewModel.observeShareCount().observe(viewLifecycleOwner, Observer {
        })
    }

    override fun onItemSelected(position: Int, item: Post, isComment: Boolean) {

    }

    override fun like(position: Int, item: Post) {
        item.id?.let {
            viewModel.like(it)
        }
    }

    override fun unlike(position: Int, item: Post) {
        item.id?.let {
            viewModel.unlike(it)
        }
    }

    override fun view(position: Int, item: Post) {
        item.id?.let {
                if(item.has_view == true || item.id == null) return
                val posts = sharedPreferences.getObject(VIEWED_POSTS, PostsViewRequest::class.java)?.posts ?: arrayListOf()
                if(posts?.contains(item.id!!) == false){
                    posts.add(item.id!!)
                    sharedPreferences.putObject(VIEWED_POSTS, PostsViewRequest(posts = posts))
                }
        }
    }

    override fun share(position: Int, item: Post) {
        viewModel.shareCount(item.id ?: 0)
        var url = item?.url_share
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + url);
        try {
            startActivity(Intent.createChooser(shareIntent,"Compartilhar"))
        } catch (e: Exception) {
            e.printStackTrace()
            recycler.snack("Nenhum aplicativo para compartilhar instalado", R.color.colorSnackError, {})
        }
    }

    override fun action(position: Int, item: Post) {
        if(item.call_to_action_link.isNullOrEmpty() == false){
            item.call_to_action_link.toString().loadOnExternalBrowser(requireActivity())
        }else{
            onItemSelected(position, item)
        }
    }

    override fun openFile(position: Int, item: Post) {
        if(item.file.isNullOrEmpty() == false){
            var fileUrl = item.file.toString()
            fileUrl.loadOnExternalBrowser(requireActivity())
        }
    }

    override fun onReport(position: Int, item: Post) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tem certeza que deseja denunciar essa publicação?")
            .setPositiveButton("Sim", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    viewModel.report(item.id ?: 0)
                    recycler.snack("Publicação denunciada. Nossa equipe irá analisar esse conteúdo.", R.color.colorAccent, {})
                }
            })
            .setNegativeButton("Não", object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }

            })
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onDelete(position: Int, item: Post) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tem certeza que deseja remover essa publicação?")
            .setPositiveButton("Sim", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    viewModel.delete(item.id ?: 0)
                }
            })
            .setNegativeButton("Não", object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }

            })
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onBuy(position: Int, item: Post) {
        TODO("Not yet implemented")
    }


}