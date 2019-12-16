package com.example.app.api


@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Int,
    val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(NetworkStatus.SUCCESS)
        val LOADING = NetworkState(NetworkStatus.LOADING)
        val EMPTY = NetworkState(NetworkStatus.EMPTY)
        fun error(msg: String?) = NetworkState(NetworkStatus.FAILED, msg)
    }
}