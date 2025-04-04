package com.hg.crs.demo07.net

import com.hg.crs.demo07.entity.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApi {

    @GET("article")
    suspend fun searchArticles(
        @Query("key") key: String
    ): List<Article>
}