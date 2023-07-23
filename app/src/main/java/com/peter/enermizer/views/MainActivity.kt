package com.peter.enermizer.views

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.peter.enermizer.databinding.ActivityMainBinding
import com.peter.enermizer.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        controllerListeners()
    }

    private fun initialize() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

    }

    private fun controllerListeners() {
        binding.btnOn.setOnClickListener {
            tempCallToBulbOn()
        }

        binding.btnOff.setOnClickListener {
            tempCallToBulbOff()
        }
    }

    fun tempCallToBulbOn() {
        viewModel.callBulbOnService()
        viewModel.observeResponseLiveData().observe(this, Observer { response ->
            Log.e(TAG, "Response from BulbOn")
            Log.e(TAG, response.toString())
            Log.e(TAG, response.toString())
        })
    }
    fun tempCallToBulbOff() {
        viewModel.callBulbOffService()
        viewModel.observeResponseLiveData().observe(this) { response ->
            Log.e(TAG, "Response from BulbOff")
            Log.e(TAG, response.toString())
            Log.e(TAG, response.toString())
        }
    }
}