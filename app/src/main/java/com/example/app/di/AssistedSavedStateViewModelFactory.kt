package com.example.app.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * Base interface for all ViewModel factories
 */
interface AssistedSavedStateViewModelFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}