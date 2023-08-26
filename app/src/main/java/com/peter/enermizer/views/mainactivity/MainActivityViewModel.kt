package com.peter.enermizer.views.mainactivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peter.enermizer.services.RaspberryPiResponse
import com.peter.enermizer.services.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var job: Job? = null
var raspberryPi = MutableLiveData<Boolean?>()


class MainActivityViewModel: ViewModel() {
    fun startContinuousTask() {
        job = viewModelScope.launch {
            while (true) {
                // Perform your continuous task here
                // This could be network requests, database operations, etc.
                checkRaspBerryPiStatus()
                delay(5000) // Delay for 5 minutes before the next iteration
            }
        }
    }

    fun stopContinuousTask() {
        job?.cancel()
    }

    private fun checkRaspBerryPiStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance.apiInstance.getAPIStatus().enqueue(object :
                Callback<RaspberryPiResponse> {
                override fun onResponse(
                    call: Call<RaspberryPiResponse>,
                    response: Response<RaspberryPiResponse>
                ) {
                    if (response.body() != null) {
                        raspberryPi.value = response.body()!!.status
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponse>, t: Throwable) {
                    Log.d("TAG", t.message.toString())
                    raspberryPi.value = false
                }
            })
        }
    }

    fun observeResponseLiveData(): MutableLiveData<Boolean?> {
        return raspberryPi
    }

}