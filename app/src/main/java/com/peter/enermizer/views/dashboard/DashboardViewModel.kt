package com.peter.enermizer.views.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.enermizer.data.ErrorObject
import com.peter.enermizer.services.RaspberryPiResponse
import com.peter.enermizer.services.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

/*
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
*/

    private var raspberryPiResponse = MutableLiveData<RaspberryPiResponse>()
    private var socketStatus = MutableLiveData<RaspberryPiResponse>()
    private var _errorStatus: MutableLiveData<ErrorObject> =  MutableLiveData<ErrorObject>()
    val errorStatus = _errorStatus

    fun callSocketController(socketNumber: Int, socketStatus: Int) {
        RetrofitInstance.apiInstance.postRelayController(socketNumber, socketStatus).enqueue(object : Callback<RaspberryPiResponse> {
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
                _errorStatus.value = ErrorObject("Failed to control socket (Socket Number: $socketNumber &  SocketStatus: $socketStatus) Try later", true)
            }
        })

    }

    fun checkSocketController(socketNumber: Int) {
        RetrofitInstance.apiInstance.getRelayController(socketNumber).enqueue(object : Callback<RaspberryPiResponse> {
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
                _errorStatus.value = ErrorObject("Failed to control socket (Socket Number: $socketNumber) Try later", true)
            }
        })

    }

    fun observeResponseLiveData(): LiveData<RaspberryPiResponse> {
        return raspberryPiResponse
    }

    fun observeSocketStatus(): LiveData<RaspberryPiResponse> {
        return socketStatus
    }

}