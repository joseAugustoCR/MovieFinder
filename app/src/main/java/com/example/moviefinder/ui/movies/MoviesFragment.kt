package com.example.moviefinder.ui.movies

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.GridLayoutManager

import com.example.moviefinder.R
import com.example.moviefinder.SessionManager
import com.example.moviefinder.api.AuthResource
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.base.NAVIGATION_RESULT_OK
import com.example.moviefinder.api.Movie
import com.example.moviefinder.api.NetworkStatus
import com.example.moviefinder.api.User
import com.example.moviefinder.di.feature.Feature
import com.example.moviefinder.di.ViewModelProviderFactory
import com.example.moviefinder.utils.SharedPreferencesManager
import com.example.moviefinder.utils.navigation.NavigationResult
import com.example.moviefinder.utils.navigation.NavigationResultListener
import com.example.moviefinder.utils.toLiveData
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
    @Inject lateinit var feature: Feature
    @Inject lateinit var sharedPreferences:SharedPreferencesManager
    @Inject lateinit var sessionManager: SessionManager

    override fun onItemSelected(position: Int, item: Movie) {
        navController.navigate(MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(item))

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
        d{"feature ${feature}"}
        sharedPreferences.putObject("user", User(name = "José A"))
        val savedUser = sharedPreferences.getObject("user", User::class.java)
        d{"user from sp = "}

        sessionManager.getAuthUser().observe(viewLifecycleOwner, Observer {
            if(it.status == AuthResource.AuthStatus.AUTHENTICATED){
                d{"logged"}
            }else{
                d{"logout"}
            }
        })

        sessionManager.login(savedUser)

        logoutBtn.setOnClickListener {
            sessionManager.logout()
        }




    }

    fun setListeners(){
        searchView.setOnClickListener {
            navigateForResult(REQUEST_SEARCH, MoviesFragmentDirections.actionMoviesFragmentToSearchFragment(queryText.text.toString()))
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
        viewModel.getMovies().observe(viewLifecycleOwner, Observer {
            moviesAdapter.submitList(it)
        })

        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            if(it.status == NetworkStatus.FAILED){
                Snackbar.make(recycler, "Ops, something went wrong.", Snackbar.LENGTH_SHORT).show()
            }
        })

        viewModel.initialLoad.observe(viewLifecycleOwner, Observer {
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

    fun setSearchLayout(query:String?){
        if(query.isNullOrEmpty()){
            queryText.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryTextColor))
            queryText.text = ""
            backBtn.visibility = View.GONE
        }else{
            queryText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textColor))
            queryText.text = query
            backBtn.visibility = View.VISIBLE
        }
    }

    fun search(query:String?){
        moviesAdapter.submitList(null)
        loadingLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
        emptyLayout.visibility = View.GONE
        viewModel.performSearch(query)
        setSearchLayout(query)
    }





}
