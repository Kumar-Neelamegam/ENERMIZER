package com.peter.enermizer.views.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.peter.enermizer.data.ErrorObject
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.data.RelaySettingsDataObject
import com.peter.enermizer.services.RetrofitInstance
import com.peter.enermizer.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }
    val text: LiveData<String> = _text
    private var _errorStatus: MutableLiveData<ErrorObject> = MutableLiveData<ErrorObject>()
    val errorStatus = _errorStatus

    fun postRelaySettings(ipaddress: String, relay1Power: String, relay2Power: String) {
        val jsonData = "{ \"relay1Power\": \"$relay1Power\", \"relay2Power\": \"$relay2Power\" }"
        val dataObject = Gson().fromJson(jsonData, RelaySettingsDataObject::class.java)
        RetrofitInstance(Common.buildIpaddress(ipaddress)).apiInstance.postRelaySettings(dataObject)
            .enqueue(object :
                Callback<RaspberryPiResponseDataset> {
                override fun onResponse(
                    call: Call<RaspberryPiResponseDataset>,
                    response: Response<RaspberryPiResponseDataset>
                ) {
                    if (response.body() == null) {
                        _errorStatus.value =
                            ErrorObject("Failed to save settings to server! Try later", true)
                        return
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponseDataset>, t: Throwable) {
                    Log.e("TAG", t.message.toString())
                    _errorStatus.value =
                        ErrorObject("Failed to save settings to server! Try later", true)
                }

            })
    }
}