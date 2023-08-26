package com.peter.enermizer.services

import com.peter.enermizer.BuildConfig.ServiceIPAddress
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.data.ReportDataObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private val BASE_URL = "http://$ServiceIPAddress/api/"

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
    val apiInstance: RaspberryAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RaspberryAPIService::class.java)
    }
}
