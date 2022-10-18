package com.example.newzy.network

data class NewsMain (
    val status: String,
    val totalResults: Int,
    val articles: List<Articles>
)