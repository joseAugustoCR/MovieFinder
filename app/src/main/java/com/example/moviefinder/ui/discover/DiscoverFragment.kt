package com.example.moviefinder.ui.discover

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
import com.example.moviefinder.networking.DiscoverMovie
import com.example.moviefinder.networking.Resource
import com.example.moviefinder.utils.ViewModelProviderFactory
import kotlinx.android.synthetic.main.discover_fragment.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment(), DiscoverMoviesAdapter.Interaction {
    private lateinit var viewModel: DiscoverViewModel
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onItemSelected(position: Int, item: DiscoverMovie) {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.discover_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(DiscoverViewModel::class.java)
        subscriveObservers()
    }


    fun subscriveObservers(){
        viewModel.observeDiscover().removeObservers(this)
        viewModel.observeDiscover().observe(this, Observer {
            when(it.status){
                Resource.Status.SUCCESS ->{
                    var adapter = DiscoverMoviesAdapter()
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
