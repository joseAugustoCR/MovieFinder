package com.example.moviefinder.ui.moviedetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.api.Movie
import com.example.moviefinder.api.Resource
import com.example.moviefinder.di.ViewModelProviderFactory
import com.example.moviefinder.di.feature.Feature
import com.example.moviefinder.utils.*
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.movie_details_fragment.*
import javax.inject.Inject

class MovieDetailsFragment : BaseFragment() {

    private lateinit var viewModel: MovieDetailsViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    val args:MovieDetailsFragmentArgs by navArgs()
    var state = MediatorLiveData<Resource<Any>>()
    var movie: Movie? = null
    @Inject lateinit var feature: Feature



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MovieDetailsViewModel::class.java)
        movie = args.movie
        setupToolbar()
        setData()
        subscribeObservers()
        d{"feature ${feature}"}

    }

    fun setData(){
        movie?.let {
            mainImg.load(Constants.IMAGE_BASE_URL + PosterSize.w500 + it.poster_path, crop = true, fade = true)
            coverImg.load(Constants.IMAGE_BASE_URL + BackdropSize.w780 + it.backdrop_path, crop = true, fade = true)
            movieTitle.text = it.title
            overview.text = it.overview
            rate.text = it.vote_average.toString()
            releaseDate.text = it.release_date?.substring(0,4)
        }

    }

    override fun onResume() {
        super.onResume()
        args.movie.id?.let { viewModel.fetchMovie(it) }
    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        setHasOptionsMenu(true)
        toolbar.title = args.movie.title
    }

    fun subscribeObservers(){
        val movieID = args.movie.id
        if(movieID == null) return
        viewModel.getMovie().removeObservers(viewLifecycleOwner)
        viewModel.getMovie().observe(viewLifecycleOwner, Observer {
            //receive null value in case of loading or error
            // ideally we should handle by state, but in this case we use the arg value received
            if(it.data == null) return@Observer
            movie = it.data
            setData()
            d{"movie details ${movie.toString()}"}
        })
    }



}
