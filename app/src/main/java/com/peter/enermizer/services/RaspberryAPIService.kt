package com.peter.enermizer.services

import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.data.ReportDataObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


private val BASE_URL = "http://192.168.1.116:8080/api/"

interface RaspberryAPIService {
    @GET("status")
    fun getAPIStatus(): Call<RaspberryPiResponseDataset>

    @POST("relaycontroller/{relayNumber}/{relayStatus}")
    fun postRelayController(
        @Path("relayNumber") relayNumber: Int,
        @Path("relayStatus") relayStatus: Int
    ): Call<RaspberryPiResponseDataset>

    @POST("relaystatus/{relayNumber}")
    fun getRelayController(
        @Path("relayNumber") relayNumber: Int
    ): Call<RaspberryPiResponseDataset>

    @POST("getAllReports")
    fun getCombinedReports(@Body data: ReportDataObject): Call<RaspberryPiResponseDataset>


}

object RetrofitInstance {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiInstance: RaspberryAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RaspberryAPIService::class.java)
    }
}
