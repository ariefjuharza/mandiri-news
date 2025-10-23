package dev.rakamin.newsapp.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("articles")
    val articles: List<Articles>,

    @SerializedName("status")
    val status: String,

    @SerializedName("totalResults")
    val totalResults: Int
)
