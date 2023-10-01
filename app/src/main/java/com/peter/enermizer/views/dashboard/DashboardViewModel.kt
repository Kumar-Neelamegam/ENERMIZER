package com.peter.enermizer.views.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peter.enermizer.data.ErrorObject
import com.peter.enermizer.data.RaspberryPiRelayDataset
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.services.RetrofitInstance
import com.peter.enermizer.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    var raspberryPiResponse = MutableLiveData<RaspberryPiResponseDataset>()
    private var relayStatus = MutableLiveData<RaspberryPiRelayDataset>()
    private var _errorStatus: MutableLiveData<ErrorObject> = MutableLiveData<ErrorObject>()
    val errorStatus = _errorStatus

    fun callRelayController(relayNumber: Int, relayStatus: Int) {
        RetrofitInstance(Common.GLOBAL_IP_ADDRESS!!).apiInstance.postRelayController(relayNumber, relayStatus)
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

    fun checkRelayController(relayNumber: Int) {
        RetrofitInstance(Common.GLOBAL_IP_ADDRESS!!).apiInstance.getRelayController(relayNumber)
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