package com.example.app.utils

public class Constants(){
    companion object {
        val BASE_URL = "https://api-tomoto.entregadigital.app.br/api/v1/"
//        val TEST_AUTHENTICATION = "Bearer dyUkpEamNd37fVgLEAhj3boYUec4bdfM982qOvc4VU4e1HR4kYyB5NMEAH77Ct8XgMUL48FUEMrXCNq3"
//        val API_KEY = "1fad6d580fb2af7cbc0b0d13f3529834"
//        val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        val CACHE_SIZE = 5 * 1024 * 1024
        val MAX_AGE = 30
        val MAX_STALE = 7*24*60*60
        val TIMEOUT: Long = 20
        val PAGINATION_SIZE = 10


        val LOGIN_REQUIRED = false
        val REGISTER_ENABLED = true
        val BUY_EXTERNAL_URL = true

        val PROFILE_IMG_MAX_WIDTH = 600
        val POST_IMG_MAX_WIDTH = 1000
        val DEFAULT_IMG_MAX_WIDTH = 800


        val S3_BUCKET = "rqx/photos"
        val S3_BUCKET_PROFILE = "rqx/perfil"
        val S3_BUCKET_POSTS = "rqx/posts"
    }

}


