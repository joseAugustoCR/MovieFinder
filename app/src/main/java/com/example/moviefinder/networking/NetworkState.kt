package com.example.moviefinder.networking

import com.example.daggersample.networking.NetworkStatus

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: NetworkStatus,
    val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(NetworkStatus.SUCCESS)
        val LOADING = NetworkState(NetworkStatus.RUNNING)
        fun error(msg: String?) = NetworkState(NetworkStatus.FAILED, msg)
    }
}