package com.peter.enermizer.views.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportsViewModel : ViewModel() {

 /*   private val _text = MutableLiveData<String>().apply {
        value = "This is reports Fragment"
    }
    val text: LiveData<String> = _text*/


    fun getReportsBasedOnDates(startDate:String, endDate:String):List<String> {

        return listOf()
    }


}