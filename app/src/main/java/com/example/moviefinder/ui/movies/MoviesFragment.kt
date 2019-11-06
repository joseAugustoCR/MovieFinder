package com.example.moviefinder.ui.movies

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.base.NAVIGATION_RESULT_OK
import com.example.moviefinder.networking.Movie
import com.example.moviefinder.networking.NetworkState
import com.example.moviefinder.networking.NetworkStatus
import com.example.moviefinder.utils.ViewModelProviderFactory
import com.example.moviefinder.utils.navigation.NavigationResult
import com.example.moviefinder.utils.navigation.NavigationResultListener
import com.github.ajalt.timberkt.d
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.movies_fragment.*
import kotlinx.android.synthetic.main.searchview.*
import javax.inject.Inject

private const val REQUEST_SEARCH = 1
class MoviesFragment : BaseFragment(), MoviesAdapter.Interaction, NavigationResultListener {
    private lateinit var viewModel: MoviesViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var moviesAdapter:MoviesAdapter

    override fun onItemSelected(position: Int, item: Movie) {
        navController.navigate(MoviesFragmentDirections.actionDiscoverFragmentToMovieDetailsFragment(item))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movies_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MoviesViewModel::class.java)
        initRecycler()
        setObservers()
        setListeners()
    }

    fun setListeners(){
        searchView.setOnClickListener {
            navigateForResult(REQUEST_SEARCH, MoviesFragmentDirections.actionMoviesFragmentToSearchFragment())
        }

        backBtn.setOnClickListener {
            search(null)
        }
    }

    fun initRecycler(){
        with(recycler){
            layoutManager = GridLayoutManager(activity as Context, 3, GridLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = moviesAdapter
        }

    }

    fun setObservers(){
        viewModel.getMovies()

        viewModel.listLiveData?.observe(this, Observer {
            if(it.isEmpty() && moviesAdapter.itemCount == 0){
                emptyLayout.visibility = View.VISIBLE
            }else{
                emptyLayout.visibility = View.GONE
            }
            moviesAdapter.submitList(it)

        })

        viewModel.query.observe(this, Observer {
            queryText.text = it
            if(it.isNullOrEmpty()){
                queryText.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryTextColor))
                queryText.text = "Search for movies..."
                backBtn.visibility = View.GONE
            }else{
                queryText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textColor))
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
            if(it.status == NetworkStatus.EMPTY){
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
        moviesAdapter.submitList(null)
        loadingLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        emptyLayout.visibility = View.GONE
        viewModel.search(query)
    }





}
