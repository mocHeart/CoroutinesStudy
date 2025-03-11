package com.hg.crs.demo01

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

data class City(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory()) // 添加 Kotlin 支持
    .build()

val userServiceApi: UserServiceApi by lazy {
    val retrofit = retrofit2.Retrofit.Builder()
        .client(OkHttpClient().newBuilder().addInterceptor {
            it.proceed(it.request()).apply {
                Log.i("JY>", "Request URL: ${it.request().url()}")
                Log.i("JY>", "code: ${code()}")
            }
        }.build())
        // 返回JSON串: [{"id":1,"name":"北京"}, {"id":2,"name":"上海"}]
        .baseUrl("http://guolin.tech/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    retrofit.create(UserServiceApi::class.java)
}

interface UserServiceApi {
    @GET("/api/china/")
    fun loadUser(): Call<List<City>>
}