package com.peter.enermizer.views.mainactivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.services.RetrofitInstance
import com.peter.enermizer.utils.Common
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


class MainActivityViewModel : ViewModel() {
    fun startPiningRaspberryPi() {
        job = viewModelScope.launch {
            while (true) {
                // Perform your continuous task here
                // This could be network requests, database operations, etc.
                checkRaspBerryPiStatus()
                delay(60000) // Delay for 1 minutes before the next iteration
            }
        }
    }

    fun stopPiningRaspberryPi() {
        job?.cancel()
    }

    private fun checkRaspBerryPiStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitInstance(Common.GLOBAL_IP_ADDRESS!!).apiInstance.getAPIStatus().enqueue(object :
                Callback<RaspberryPiResponseDataset> {
                override fun onResponse(
                    call: Call<RaspberryPiResponseDataset>,
                    response: Response<RaspberryPiResponseDataset>
                ) {
                    if (response.body() != null) {
                        raspberryPi.value = response.body()!!.status
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponseDataset>, t: Throwable) {
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