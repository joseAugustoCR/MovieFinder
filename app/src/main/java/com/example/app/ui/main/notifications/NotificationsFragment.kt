package com.example.app.ui.main.notifications

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.ui.NavigationUI
import com.example.app.R
import com.example.app.api.Notification
import com.example.app.api.Post
import com.example.app.api.Resource
import com.example.app.base.BaseFragment
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.ui.main.community.CommunityViewModel
import com.example.app.utils.extensions.loadOnBrowser
import kotlinx.android.synthetic.main.collapsing_appbar_regular.*
import kotlinx.android.synthetic.main.error_state.*
import kotlinx.android.synthetic.main.notifications_fragment.*
import javax.inject.Inject

class NotificationsFragment : BaseFragment(), NotificationsAdapter.Interaction {
    @Inject
    lateinit var savedStateFactory: InjectingSavedStateViewModelFactory
    val adapter = NotificationsAdapter(this)


    companion object {
        fun newInstance() = NotificationsFragment()
    }

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notifications_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[NotificationsViewModel::class.java]
        setupToolbar()
        setRecycler()
        setListeners()
        setObservers()
        getNotifications()
    }

    fun getNotifications(){
        viewModel.getNotifications()
    }

    fun setRecycler(){
        recycler.adapter = adapter
    }

    fun setListeners(){
        tryAgainBtn.setOnClickListener {
            getNotifications()
        }
    }


    fun setData(data: ArrayList<Notification>?){
        adapter.submitList(data)
        if(data.isNullOrEmpty()){
            emptyLayout.visibility = View.VISIBLE
        }else{
            emptyLayout.visibility = View.GONE
        }
    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        collapsingToolbar.title = "Notificações"
        setHasOptionsMenu(true)
    }

    fun setObservers(){
        viewModel.observeNotifications().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.GONE
                setData(it.data)

            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
                emptyLayout.visibility = View.GONE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE
            }
        })
    }

    override fun onItemSelected(position: Int, item: Notification) {
        val url = item.url
        if(url.isNullOrEmpty() == true) {
            return
        }
        val uri = url.toString().toUri()
        if(uri.scheme.equals("rqx")){
            if(uri.host.toString().contains("timeline")){
                val id = uri.lastPathSegment?.toIntOrNull()
                if(id != null){
                    safeNavigate(navController, NotificationsFragmentDirections.actionNotificationsFragmentToPostDetailFragment(
                        Post(id = id)
                    ))
                    return
                }
            }
        }else{
            url.toString().loadOnBrowser(requireActivity())
        }
    }

}