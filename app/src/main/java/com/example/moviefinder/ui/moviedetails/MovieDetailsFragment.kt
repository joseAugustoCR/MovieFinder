package com.example.moviefinder.ui.moviedetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.utils.*
import com.github.ajalt.timberkt.d
import com.google.gson.Gson
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.movie_details_fragment.*
import javax.inject.Inject

class MovieDetailsFragment : BaseFragment() {

    private lateinit var viewModel: MovieDetailsViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    val args:MovieDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(MovieDetailsViewModel::class.java)
        setupToolbar()
        setData()
    }

    fun setData(){
        mainImg.load(Constants.IMAGE_BASE_URL + PosterSize.w500 + args.movie.poster_path, crop = true, fade = true)
        coverImg.load(Constants.IMAGE_BASE_URL + BackdropSize.w780 + args.movie.backdrop_path, crop = true, fade = true)
        movieTitle.text = args.movie.title
        overview.text = args.movie.overview
        rate.text = args.movie.vote_average.toString()
        releaseDate.text = args.movie.release_date?.substring(0,4)
        d{ Gson().toJson(args.movie) }
    }


    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        setHasOptionsMenu(true)
        toolbar.title = args.movie.title
    }




}
