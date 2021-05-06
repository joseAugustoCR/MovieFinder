package com.example.app.utils

public class Constants(){
    companion object {
        val BASE_URL = "https://rqx2.dev.aioria.com.br/api/v3/"
        val TEST_AUTHENTICATION = "Bearer dyUkpEamNd37fVgLEAhj3boYUec4bdfM982qOvc4VU4e1HR4kYyB5NMEAH77Ct8XgMUL48FUEMrXCNq3"
//        val API_KEY = "1fad6d580fb2af7cbc0b0d13f3529834"
//        val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        val CACHE_SIZE = 5 * 1024 * 1024
        val MAX_AGE = 30
        val MAX_STALE = 7*24*60*60
        val TIMEOUT: Long = 20
        val PAGINATION_SIZE = 10


        val LOGIN_REQUIRED = false
        val BUY_EXTERNAL_URL = true
    }

}


