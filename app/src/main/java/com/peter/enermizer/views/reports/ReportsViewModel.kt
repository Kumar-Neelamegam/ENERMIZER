package com.peter.enermizer.views.reports

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.peter.enermizer.data.ErrorObject
import com.peter.enermizer.data.ReportDataObject
import com.peter.enermizer.data.RaspberryPiResponseDataset
import com.peter.enermizer.services.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportsViewModel : ViewModel() {

    /*   private val _text = MutableLiveData<String>().apply {
           value = "This is reports Fragment"
       }
       val text: LiveData<String> = _text*/
    private var raspberryPiResponse = MutableLiveData<RaspberryPiResponseDataset>()
    private var _errorStatus: MutableLiveData<ErrorObject> = MutableLiveData<ErrorObject>()
    val errorStatus = _errorStatus

    fun getReportsBasedOnDates(fromDate: String, toDate: String) {

        val json = """
                    {
                    "fromDate_aws": "$fromDate",
                    "toDate_aws": "$toDate",
                    "fromDate_sm": "$fromDate",
                    "toDate_sm": "$toDate"
                    }
                    """
        val dateObject = Gson().fromJson(json, ReportDataObject::class.java)
        RetrofitInstance.apiInstance.getCombinedReports(dateObject)
            .enqueue(object : Callback<RaspberryPiResponseDataset> {
                override fun onResponse(
                    call: Call<RaspberryPiResponseDataset>,
                    response: Response<RaspberryPiResponseDataset>
                ) {
                    if (response.body() != null) {
                        raspberryPiResponse.value = response.body()!!
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<RaspberryPiResponseDataset>, t: Throwable) {
                    Log.e("TAG", t.message.toString())
                    _errorStatus.value = ErrorObject("Failed to retrieve reports! Try later", true)
                }
            })

    }

    fun observeResponseLiveData(): LiveData<RaspberryPiResponseDataset> {
        return raspberryPiResponse
    }


}

