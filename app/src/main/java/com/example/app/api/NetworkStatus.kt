package com.example.app.api

class NetworkStatus {

    companion object {
        const val UNAUTHORIZED = 0
        const val NO_INTERNET = 1
        const val LOADING = 2
        const val FAILED = 3
        const val SUCCESS = 4
        const val EMPTY = 5
    }
}