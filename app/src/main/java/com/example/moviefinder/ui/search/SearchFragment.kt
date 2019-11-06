package com.example.moviefinder.ui.search

import android.app.Activity
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.base.NAVIGATION_RESULT_OK
import com.example.moviefinder.ui.movies.MoviesViewModel
import com.example.moviefinder.utils.ViewModelProviderFactory
import kotlinx.android.synthetic.main.search_fragment.*
import javax.inject.Inject

class SearchFragment : BaseFragment() {
    private lateinit var viewModel: SearchViewModel
    @Inject lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.search_fragment, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(SearchViewModel::class.java)
        setupToolbar()
    }

    fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(searchMenuItem) as SearchView
        searchView.setIconifiedByDefault(true)
        searchView.isIconified = false
        searchView.findViewById<View?>(androidx.appcompat.R.id.search_plate)?.background = null
        searchView.queryHint = "Search for a movie..."
        searchView.setOnCloseListener {
            navController.navigateUp()
        }
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                navController.navigateUp()
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.isNullOrEmpty()) return false
                navigateBackWithResult(NAVIGATION_RESULT_OK, bundleOf("query" to searchView.query.toString()))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

}
