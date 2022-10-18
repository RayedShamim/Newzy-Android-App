package com.example.newzy.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://newsapi.org/v2/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface NewsApiService {
    @GET(value = "everything")
    suspend fun getNews(@Query("q") q:String,
                        @Query("pageSize") pageSize: Int?,
                        @Query("page") page: Int,
                        @Query("sortBy") sortBy: String?,
                        @Query("from") from: String?,
                        @Query("to") to: String?,
                        @Query("language") language: String?,
                        @Query("apiKey") apiKey: String = "a7d070b29a3041a58ced73c34449df8c"
    ): NewsMain

    @GET(value = "top-headlines")
    suspend fun getTopHeadlines(@Query("country") country:String,@Query("category") category: String,
    @Query("apiKey") apiKey: String = "a7d070b29a3041a58ced73c34449df8c"
    ): NewsMain
}

object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}