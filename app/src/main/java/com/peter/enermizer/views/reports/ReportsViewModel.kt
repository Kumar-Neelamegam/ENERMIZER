package com.peter.enermizer.views.reports

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.peter.enermizer.data.ErrorObject
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.data.ReportDataObject
import com.peter.enermizer.services.RetrofitInstance
import com.peter.enermizer.utils.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream


class ReportsViewModel : ViewModel() {

    /*   private val _text = MutableLiveData<String>().apply {
           value = "This is reports Fragment"
       }
       val text: LiveData<String> = _text*/
    private var raspberryPiResponse = MutableLiveData<RaspberryPiResponseDataset>()
    var raspberryPiAutoRelayResponse = MutableLiveData<ResponseBody>()

    private var _errorStatus: MutableLiveData<ErrorObject> = MutableLiveData<ErrorObject>()
    val errorStatus = _errorStatus

    fun getReportsBasedOnDates(ipaddress: String, fromDate: String, toDate: String) {

        val jsonData = "{ \"fromDate\": \"$fromDate 00:00:00\", \"toDate\": \"$toDate 23:59:59\" }"

        val dateObject = Gson().fromJson(jsonData, ReportDataObject::class.java)
        RetrofitInstance(Common.buildIpaddress(ipaddress)).apiInstance.getCombinedReports(dateObject)
            .enqueue(object : Callback<RaspberryPiResponseDataset> {
                override fun onResponse(
                    call: Call<RaspberryPiResponseDataset>,
                    response: Response<RaspberryPiResponseDataset>
                ) {
                    if (response.body() != null) {
                        raspberryPiResponse.value = response.body()!!
                    } else {
                        _errorStatus.value =
                            ErrorObject("Failed to retrieve reports! Try later", true)
                        return
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponseDataset>, t: Throwable) {
                    Log.e("TAG", t.message.toString())
                    _errorStatus.value = ErrorObject("Failed to retrieve reports! Try later", true)
                }
            })

    }

    fun getAutoRelayStatus(ipaddress: String) {
        RetrofitInstance(Common.buildIpaddress(ipaddress)).apiInstance.getLiveAutoRelayStatus()
            .enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        raspberryPiAutoRelayResponse.value = it
                    }
                } else {
                    _errorStatus.value =
                        ErrorObject("Failed to retrieve reports! Try later", true)
                    return
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                Log.e("TAG", t.message.toString())
                _errorStatus.value = ErrorObject("Failed to retrieve reports! Try later", true)
            }
        })
    }


    fun observeResponseLiveData(): LiveData<RaspberryPiResponseDataset> {
        return raspberryPiResponse
    }

    fun observeResponseLiveData2(): LiveData<ResponseBody> {
        return raspberryPiAutoRelayResponse
    }



}

