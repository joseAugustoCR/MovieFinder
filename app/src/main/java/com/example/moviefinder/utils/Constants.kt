package com.example.moviefinder.utils

public class Constants(){
    companion object {
        val BASE_URL = "https://api.themoviedb.org/3/"
        val API_KEY = "1fad6d580fb2af7cbc0b0d13f3529834"
        val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
        val CACHE_SIZE = 5 * 1024 * 1024
        val MAX_AGE = 30
        val MAX_STALE = 7*24*60*60
        val TIMEOUT: Long = 10
    }

}

class PosterSize{
    companion object {
        val original = "original/"
        val w500 = "w500/"
        val w780 = "w780/"
    }
}


class LogoSize{
    val original = "original/"
    val w500 = "w500/"
    val w300 = "w300/"
}

class BackdropSize{
    val original = "original/"
    val w780 = "w780/"
    val w300 = "w300/"
}