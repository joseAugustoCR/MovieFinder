package com.example.moviefinder.ui.movies

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.GridLayoutManager

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.networking.DiscoverMovie
import com.example.moviefinder.networking.Resource
import com.example.moviefinder.utils.ViewModelProviderFactory
import kotlinx.android.synthetic.main.movies_fragment.*
import javax.inject.Inject

class MoviesFragment : BaseFragment(), MoviesAdapter.Interaction {
    private lateinit var viewModel: MoviesViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    @Inject lateinit var adapter:MoviesAdapter

    var teste = "teste"

    override fun onItemSelected(position: Int, item: DiscoverMovie) {
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
        subscriveObservers()
    }


    fun subscriveObservers(){
        viewModel.observeDiscover().removeObservers(this)
        viewModel.observeDiscover().observe(this, Observer {
            when(it.status){
                Resource.Status.SUCCESS ->{
                    adapter.submitList(it.data?.results!!)
                    recycler.layoutManager = GridLayoutManager(activity as Context, 3, GridLayoutManager.VERTICAL, false)
                    recycler.adapter = adapter
                }
                Resource.Status.LOADING ->{

                }
                Resource.Status.ERROR ->{

                }

            }
        })
    }


}
