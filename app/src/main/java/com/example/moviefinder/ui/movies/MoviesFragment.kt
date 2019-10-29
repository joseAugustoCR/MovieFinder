package com.example.moviefinder.ui.movies

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.daggersample.networking.NetworkStatus

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.networking.Movie
import com.example.moviefinder.networking.NetworkState
import com.example.moviefinder.utils.ViewModelProviderFactory
import com.github.ajalt.timberkt.d
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.movies_fragment.*
import javax.inject.Inject

class MoviesFragment : BaseFragment(), MoviesAdapter.Interaction {
    private lateinit var viewModel: MoviesViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var adapter:MoviesAdapter

    var teste = "teste"

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
    }

    fun initRecycler(){
        recycler.layoutManager = GridLayoutManager(activity as Context, 3, GridLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    fun setObservers(){
        viewModel.getMovies()
        viewModel.test()

        viewModel.listLiveData?.observe(this, Observer {
            adapter.submitList(it)
//            if(it.isEmpty()){
//                emptyLayout.visibility = View.VISIBLE
//
//            }else{
//                emptyLayout.visibility = View.GONE
//
//            }
        })

        viewModel.response2?.observe(this, Observer {
            d{"response2 " + it.toString()}
        })
        viewModel.test()

        viewModel.networkState?.observe(this, Observer {
            if(it.status == NetworkStatus.FAILED){
                Snackbar.make(recycler, "Ops, something went wrong.", Snackbar.LENGTH_SHORT).show()
            }
        })

        viewModel.initialLoad?.observe(this, Observer {
            when(it){
                NetworkState.LOADING ->{
                    loadingLayout.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    emptyLayout.visibility = View.GONE

                }
                NetworkState.LOADED ->{
                    loadingLayout.visibility = View.GONE
                    errorLayout.visibility = View.GONE
                    emptyLayout.visibility = View.GONE

                }
                else -> {
                    if (it.status == NetworkStatus.FAILED) {
                        loadingLayout.visibility = View.GONE
                        errorLayout.visibility = View.VISIBLE
                        emptyLayout.visibility = View.GONE

                    }
                }
            }
        })
    }


}
