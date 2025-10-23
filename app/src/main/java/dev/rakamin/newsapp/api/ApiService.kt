package dev.rakamin.newsapp.api

import dev.rakamin.newsapp.model.NewsResponse
import dev.rakamin.newsapp.utils.Constants.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("category") category: String = "business",
        @Query("apiKey") apiKey: String = API_KEY
    ): Call<NewsResponse>

    @GET("everything")
    fun getAllNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = API_KEY
    ): Call<NewsResponse>

}