package com.hg.crs.demo01.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class User(val name: String, val city: String)

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory()) // 添加 Kotlin 支持
    .build()

val userServiceApi: UserServiceApi by lazy {
    val retrofit = retrofit2.Retrofit.Builder()
        .client(OkHttpClient().newBuilder().addInterceptor {
            it.proceed(it.request()).apply {
                Log.i("JY>", "Request: ${code()}")
            }
        }.build())
        .baseUrl("http://192.168.187.175:8990/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    retrofit.create(UserServiceApi::class.java)
}

interface UserServiceApi {

    @GET("/user")
    fun loadUser(@Query("name") name: String): Call<User>

}