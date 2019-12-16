package com.example.app.ui.main.timeline

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app.R
import com.example.app.base.BaseFragment
import com.example.app.di.ViewModelProviderFactory
import javax.inject.Inject

class TimelineFragment : BaseFragment() {
    @Inject
    lateinit var providerFactory: ViewModelProviderFactory
    private lateinit var viewModel: TimelineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.timeline_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, providerFactory).get(TimelineViewModel::class.java)

    }

}
