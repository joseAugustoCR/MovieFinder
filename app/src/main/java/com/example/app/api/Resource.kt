package com.example.app.api


class Resource<T>(val status: Status, val data: T?=null, val errors: ErrorResponse?=null, var statusCode:Int? = 0) {

    enum class Status {
        SUCCESS, ERROR, LOADING
    }

//    companion object Status{
//
//    }

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(errors: ErrorResponse?=null, statusCode: Int?=0): Resource<T> {
            return Resource(Status.ERROR, null, errors, statusCode =statusCode)
        }

        fun <T> loading(data: T?=null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}