package com.example.app.ui.main.product_detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.app.R
import com.example.app.api.Product
import com.example.app.base.BaseFragment
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.ui.main.community.CommunityViewModel
import com.example.app.utils.extensions.load
import kotlinx.android.synthetic.main.collapsing_appbar_product_detail.*
import javax.inject.Inject

class ProductDetailFragment : BaseFragment() {
    @Inject
    lateinit var savedStateFactory: InjectingSavedStateViewModelFactory
    val args:ProductDetailFragmentArgs by navArgs()
    var product:Product?=null

    companion object {
        fun newInstance() = ProductDetailFragment()
    }

    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_detail_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[ProductDetailViewModel::class.java]
        product = args.product
        setInitialLoad()

    }

    fun setInitialLoad(){
        coverImg.load(product?.thumb.toString())

    }

}