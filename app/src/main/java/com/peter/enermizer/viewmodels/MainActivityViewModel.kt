package com.peter.enermizer.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.enermizer.services.RaspberryPiResponse
import com.peter.enermizer.services.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    private var raspberryPiResponse = MutableLiveData<RaspberryPiResponse>()

    fun callBulbOnService() {
        RetrofitInstance.apiInstance.getBulbOn().enqueue(object : Callback<RaspberryPiResponse> {
            override fun onResponse(
                call: Call<RaspberryPiResponse>,
                response: Response<RaspberryPiResponse>
            ) {
                if (response.body() != null) {
                    raspberryPiResponse.value = response.body()!!
                } else {
                    return
                }
            }

            override fun onFailure(call: Call<RaspberryPiResponse>, t: Throwable) {
                Log.d("TAG", t.message.toString())
            }
        })

    }

    fun callBulbOffService() {
        RetrofitInstance.apiInstance.getBulbOff().enqueue(object : Callback<RaspberryPiResponse> {
            override fun onResponse(
                call: Call<RaspberryPiResponse>,
                response: Response<RaspberryPiResponse>
            ) {
                if (response.body() != null) {
                    raspberryPiResponse.value = response.body()!!
                } else {
                    return
                }
            }

            override fun onFailure(call: Call<RaspberryPiResponse>, t: Throwable) {
                Log.d("TAG", t.message.toString())
            }
        })

    }

    fun observeResponseLiveData(): LiveData<RaspberryPiResponse> {
        return raspberryPiResponse
    }

}


