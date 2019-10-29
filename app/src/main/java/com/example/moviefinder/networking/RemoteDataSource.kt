package com.example.moviefinder.networking

import com.elifox.legocatalog.api.BaseDataSource
import javax.inject.Inject

class RemoteDataSource @Inject constructor(var api: Api) : BaseDataSource() {

    fun fetch() = getResult { api.fetch(1) }
}