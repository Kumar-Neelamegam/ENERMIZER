package com.peter.enermizer.services

import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val BASE_URL = "http://192.168.1.166/api/"

interface RaspberryAPIService {
    @GET("bulbon")
    fun getBulbOn(): Call<RaspberryPiResponse>

    @GET("bulboff")
    fun getBulbOff(): Call<RaspberryPiResponse>
}

object RetrofitInstance {
    val apiInstance : RaspberryAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RaspberryAPIService::class.java)
    }
}
