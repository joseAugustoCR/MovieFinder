package com.example.app.ui.main.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.app.di.AssistedSavedStateViewModelFactory
import com.example.app.repository.CommunityRepository
import com.example.app.repository.ProductsRepository
import com.example.app.ui.main.community.CommunityViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class ProductDetailViewModel @AssistedInject constructor(private val productsRepository: ProductsRepository, @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {


    @AssistedInject.Factory
    interface Factory : AssistedSavedStateViewModelFactory<ProductDetailViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): ProductDetailViewModel  // may be ommited prior kotlin 1.3.60 or after PR #121 in AssistedInject lib
    }
}