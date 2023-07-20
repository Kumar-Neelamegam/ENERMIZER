package com.peter.enermizer.services

import okhttp3.Callback
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

private val BASE_URL = ""

interface RaspberryAPIService {
    @GET("/api/bulbon")
    suspend fun getBulbOnStatus(): Response

    @GET("/api/bulboff")
    suspend fun getBulbOffStatus(): Response

    @Headers("Content-Type: application/json")
    @POST("endpoint")
    fun sendData(@Body requestBody: RequestBody): Call<ResponseBody>
}

val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val apiService = retrofit.create(RaspberryAPIService::class.java)
