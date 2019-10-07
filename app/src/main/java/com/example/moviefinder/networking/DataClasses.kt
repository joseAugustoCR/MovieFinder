package com.example.moviefinder.networking

data class DiscoverMovie(
    var adult: Boolean? = null,
    var backdrop_path: String? = null,
    var genre_ids: List<Int?>? = null,
    var id: Int? = null,
    var original_language: String? = null,
    var original_title: String? = null,
    var overview: String? = null,
    var popularity: Double? = null,
    var poster_path: String? = null,
    var release_date: String? = null,
    var title: String? = null,
    var video: Boolean? = null,
    var vote_average: Double? = null,
    var vote_count: Int? = null
)

data class MovieDetails(
    var adult: Boolean? = null,
    var backdrop_path: String? = null,
    var belongs_to_collection: Any? = null,
    var budget: Int? = null,
    var genres: List<Genre?>? = null,
    var homepage: String? = null,
    var id: Int? = null,
    var imdb_id: String? = null,
    var original_language: String? = null,
    var original_title: String? = null,
    var overview: String? = null,
    var popularity: Double? = null,
    var poster_path: String? = null,
    var production_companies: List<ProductionCompany?>? = null,
    var production_countries: List<ProductionCountry?>? = null,
    var release_date: String? = null,
    var revenue: Int? = null,
    var runtime: Int? = null,
    var status: String? = null,
    var tagline: String? = null,
    var title: String? = null,
    var video: Boolean? = null,
    var vote_average: Double? = null,
    var vote_count: Int? = null
)

data class DiscoverMovieResponse(
    var results:List<DiscoverMovie>?=null
)

data class Genre(
    var id: Int? = null,
    var name: String? = null
)

data class ProductionCompany(
    var id: Int? = null,
    var logo_path: String? = null,
    var name: String? = null,
    var origin_country: String? = null
)

data class ProductionCountry(
    var iso_3166_1: String? = null,
    var name: String? = null
)