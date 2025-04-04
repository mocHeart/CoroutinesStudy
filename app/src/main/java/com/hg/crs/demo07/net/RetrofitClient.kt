package com.hg.crs.demo07.net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

object RetrofitClient {

    private val instance: Retrofit by lazy {
        Retrofit.Builder()
            .client(OkHttpClient.Builder().build())
            .baseUrl("http:\\xxxx")
            .build()
    }

    val articleApi: ArticleApi by lazy {
        instance.create(ArticleApi::class.java)
    }

}