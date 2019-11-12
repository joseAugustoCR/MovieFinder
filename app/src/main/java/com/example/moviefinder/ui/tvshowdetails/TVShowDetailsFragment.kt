package com.example.moviefinder.ui.tvshowdetails


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.ui.moviedetails.MovieDetailsFragmentArgs
import com.example.moviefinder.utils.BackdropSize
import com.example.moviefinder.utils.Constants
import com.example.moviefinder.utils.PosterSize
import com.example.moviefinder.utils.load
import com.github.ajalt.timberkt.d
import com.google.gson.Gson
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.movie_details_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class TVShowDetailsFragment : BaseFragment() {
    val args: TVShowDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tvshow_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
        setData()
    }

    fun setData(){
        mainImg.load(Constants.IMAGE_BASE_URL + PosterSize.w500 + args.tvshow.poster_path, crop = true, fade = true)
        coverImg.load(Constants.IMAGE_BASE_URL + BackdropSize.w780 + args.tvshow.backdrop_path, crop = true, fade = true)
        movieTitle.text = args.tvshow.name
        overview.text = args.tvshow.overview
        rate.text = args.tvshow.vote_average.toString()
        releaseDate.text = args.tvshow.first_air_date?.substring(0,4)
        d{ Gson().toJson(args.tvshow) }
    }


    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        setHasOptionsMenu(true)
        toolbar.title = args.tvshow.name
    }

}
