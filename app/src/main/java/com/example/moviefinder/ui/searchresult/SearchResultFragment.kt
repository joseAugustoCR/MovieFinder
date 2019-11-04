package com.example.moviefinder.ui.searchresult

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.moviefinder.R
import com.example.moviefinder.base.BaseFragment
import com.example.moviefinder.ui.search.SearchViewModel
import com.example.moviefinder.utils.ViewModelProviderFactory
import javax.inject.Inject

class SearchResultFragment : BaseFragment() {
    @Inject lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: SearchResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_result_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(SearchResultViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
