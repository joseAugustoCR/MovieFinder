package com.example.moviefinder.ui.tvshows

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.base.NAVIGATION_RESULT_OK
import com.example.moviefinder.api.NetworkStatus
import com.example.moviefinder.api.TVShow
import com.example.moviefinder.di.ViewModelProviderFactory
import com.example.moviefinder.utils.navigation.NavigationResult
import com.example.moviefinder.utils.navigation.NavigationResultListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.searchview.*
import kotlinx.android.synthetic.main.tvshows_fragment.*
import javax.inject.Inject
private const val REQUEST_SEARCH = 1

class TVShowsFragment : BaseFragment(), TVShowsAdapter.Interaction, NavigationResultListener {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: TVShowsViewModel
    @Inject lateinit var tvshowsAdapter: TVShowsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tvshows_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(TVShowsViewModel::class.java)
        initRecycler()
        setObservers()
        setListeners()
        setSearchView()
    }

    override fun onItemSelected(position: Int, item: TVShow) {
        navController.navigate(TVShowsFragmentDirections.actionTVShowsFragmentToTVShowDetailsFragment(item))
    }


    fun setSearchView(){
        queryText.hint = "Search for tv shows..."
    }


    fun setListeners(){
        searchView.setOnClickListener {
            navigateForResult(REQUEST_SEARCH, TVShowsFragmentDirections.actionTVShowsFragmentToSearchFragment(queryText.text.toString()))
        }

        backBtn.setOnClickListener {
            search(null)
        }
    }

    fun initRecycler(){
        with(recycler){
            layoutManager = GridLayoutManager(activity as Context, 3, GridLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = tvshowsAdapter
        }

    }

    fun setObservers(){
        viewModel.getTVShows()

        viewModel.listLiveData?.observe(this, Observer {
            tvshowsAdapter.submitList(it)

        })

        viewModel.query.observe(this, Observer {
            queryText.text = it
            if(it.isNullOrEmpty()){
                queryText.text = ""
                backBtn.visibility = View.GONE
            }else{
                queryText.text = it
                backBtn.visibility = View.VISIBLE
            }
        })


        viewModel.networkState?.observe(this, Observer {
            if(it.status == NetworkStatus.FAILED){
                Snackbar.make(recycler, "Ops, something went wrong.", Snackbar.LENGTH_SHORT).show()
            }
        })

        viewModel.initialLoad?.observe(this, Observer {
            if(it.status == NetworkStatus.SUCCESS){
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.GONE
                emptyLayout.visibility = View.GONE
            }else if (it.status == NetworkStatus.LOADING){
                loadingLayout.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
                emptyLayout.visibility = View.GONE
            }else if (it.status == NetworkStatus.FAILED){
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE

            }else if(it.status == NetworkStatus.EMPTY){
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
            }

        })
    }



    override fun onNavigationResult(result: NavigationResult) {
        if(result.requestCode == REQUEST_SEARCH && result.resultCode == NAVIGATION_RESULT_OK){
            val query = result.data?.getString("query", "").toString()
            search(query)
        }
    }

    fun search(query:String?){
        tvshowsAdapter.submitList(null)
        loadingLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        emptyLayout.visibility = View.GONE
        viewModel.search(query)
    }

}
