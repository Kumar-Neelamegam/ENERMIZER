package com.peter.enermizer.views.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peter.enermizer.data.ErrorObject
import com.peter.enermizer.data.RaspberryPiRelayDataset
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.services.RetrofitInstance
import com.peter.enermizer.utils.Common
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    var raspberryPiResponse = MutableLiveData<RaspberryPiResponseDataset>()
    private var relayStatus = MutableLiveData<RaspberryPiRelayDataset>()
    private var _errorStatus: MutableLiveData<ErrorObject> = MutableLiveData<ErrorObject>()
    val errorStatus = _errorStatus
    private var job: Job? = null

    /**
     * This is the new method to check the relay in live mode because of Auto mode this might change
     * RELAY ON = GREEN
     * RELAY OFF = RED
     * RELAY AUTO = YELLOW
     */
    fun liveCheckRelayControllers(ipaddress: String, relay1Number:Int, relay2Number: Int) {
        job = viewModelScope.launch {
            while (true) {
                checkRelayController(ipaddress, relay1Number)
                checkRelayController(ipaddress, relay2Number)
                delay(900000) // Delay for 15 minutes before the next iteration
                //delay(30000) // Delay for 30 second before the next iteration - TESTING
            }

        }
    }
    fun callRelayController(ipaddress: String, relayNumber: Int, relayStatus: Int) {

        RetrofitInstance(Common.buildIpaddress(ipaddress)).apiInstance.postRelayController(relayNumber, relayStatus)
            .enqueue(object : Callback<RaspberryPiResponseDataset> {
                override fun onResponse(
                    call: Call<RaspberryPiResponseDataset>,
                    response: Response<RaspberryPiResponseDataset>
                ) {
                    if (response.body() != null) {
                        raspberryPiResponse.value = response.body()
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponseDataset>, t: Throwable) {
                    Log.d("TAG", t.message.toString())
                    _errorStatus.value = ErrorObject(
                        "Failed to control relay (Relay Number: $relayNumber &  relayStatus: $relayStatus) Try later",
                        true
                    )
                }
            })

    }

    fun checkRelayController(ipaddress: String, relayNumber: Int) {
        RetrofitInstance(Common.buildIpaddress(ipaddress)).apiInstance.getRelayController(relayNumber)
            .enqueue(object : Callback<RaspberryPiResponseDataset> {
                override fun onResponse(
                    call: Call<RaspberryPiResponseDataset>,
                    response: Response<RaspberryPiResponseDataset>
                ) {
                    if (response.body() != null) {
                        relayStatus.value = RaspberryPiRelayDataset(
                            response.body()!!.status, "", "" , relay = relayNumber)
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponseDataset>, t: Throwable) {
                    Log.d("TAG", t.message.toString())
                    _errorStatus.value = ErrorObject(
                        "Failed to control relay (Relay Number: $relayNumber) Try later",
                        true
                    )
                }
            })
    }

    fun observeResponseLiveData(): LiveData<RaspberryPiResponseDataset> {
        return raspberryPiResponse
    }

    fun observeRelayStatus(): LiveData<RaspberryPiRelayDataset> {
        return relayStatus
    }

}