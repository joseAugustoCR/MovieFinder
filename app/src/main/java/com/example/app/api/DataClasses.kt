package com.example.app.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
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
) : Parcelable

data class Sale(
    var title: String?=null,
    var desc:String?=null,
    var image:String?=null,
    var original_price:Float?=null,
    var new_price:Float?=null,
    var button_text:String?=null,
    var button_action:String?=null
)

@Parcelize
data class TVShow(
    var backdrop_path: String? = null,
    var first_air_date: String? = null,
    var genre_ids: List<Int?>? = null,
    var id: Int? = null,
    var name: String? = null,
    var origin_country: List<String?>? = null,
    var original_language: String? = null,
    var original_name: String? = null,
    var overview: String? = null,
    var popularity: Double? = null,
    var poster_path: String? = null,
    var vote_average: Double? = null,
    var vote_count: Int? = null
) : Parcelable

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

data class WrapperPagedApiResponse<T>(
    var results: List<T>?=null,
    var total_pages: Int?=null,
    var total_results: Int?=null,
    var page: Int?=null
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

data class User(var name:String?=null, var password:String?=null)
data class ErrorResponse(var id:Int?=null)