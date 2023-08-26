package com.peter.enermizer.services

import com.peter.enermizer.BuildConfig.ServiceIPAddress
import com.peter.enermizer.data.ReportDataObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private val BASE_URL = "http://$ServiceIPAddress/api/"

interface RaspberryAPIService {
    @GET("status")
    fun getAPIStatus(): Call<RaspberryPiResponse>

    @POST("relaycontroller")
    fun postRelayController(
        @Query("relayNumber") socketNumber: Int,
        @Query("relayStatus") socketStatus: Int
    ): Call<RaspberryPiResponse>

    @POST("relaystatus")
    fun getRelayController(
        @Query("relayNumber") socketNumber: Int
    ): Call<RaspberryPiResponse>

    @POST("getAllReports")
    fun getCombinedReports(@Body data: ReportDataObject): Call<RaspberryPiResponse>


}

object RetrofitInstance {
    val apiInstance: RaspberryAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RaspberryAPIService::class.java)
    }
}
