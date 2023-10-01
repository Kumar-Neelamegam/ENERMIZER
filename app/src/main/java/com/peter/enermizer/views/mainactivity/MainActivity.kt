package com.peter.enermizer.views.mainactivity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.peter.enermizer.R
import com.peter.enermizer.databinding.ActivityMainBinding
import com.peter.enermizer.utils.ConnectionType
import com.peter.enermizer.utils.DataStoreManager
import com.peter.enermizer.utils.NetworkMonitorUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val networkMonitor = NetworkMonitorUtil(this)
    private lateinit var viewModel: MainActivityViewModel
    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_reports, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        /**
         * Check whether internet is up and running
         */
        checkInternetAndRelay()
        /**
         * Check whether settings is saved
         */
        CoroutineScope(Dispatchers.Main).launch {
            val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
            if (storedIpAddress?.isNotEmpty() == true) {
                raspberryConnectivity(storedIpAddress.toString())
            } else {
                navView.selectedItemId = R.id.navigation_settings
            }
        }

    }

    private fun raspberryConnectivity(ipaddress: String) {
        /**
         * Check whether raspberry pi is up and running
         */
        viewModel.startPiningRaspberryPi(ipaddress)
        viewModel.observeResponseLiveData().observeForever {
            if (it != null) {
                changeRaspberryPiStatus(it)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopPiningRaspberryPi()
    }

    private fun checkInternetAndRelay() {
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                Log.e("NETWORK_MONITOR_STATUS", "Wifi Connection")
                                changeInternetStatus(true)
                            }

                            ConnectionType.Cellular -> {
                                Log.e("NETWORK_MONITOR_STATUS", "Cellular Connection")
                                changeInternetStatus(false)
                            }

                            else -> {
                                Log.e("Else error", "Error neither WIFI / Cellular")
                                changeInternetStatus(false)
                            }
                        }
                    }

                    false -> {
                        Log.i("NETWORK_MONITOR_STATUS", "No Connection")
                        changeInternetStatus(false)
                    }
                }
            }
        }
    }

    private fun changeRaspberryPiStatus(status: Boolean) {

        if (status) {
            binding.raspberrypiStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_raspberrypi
                )
            )
        } else {
            binding.raspberrypiStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_raspberrypi_disabled
                )
            )
        }

    }

    private fun changeInternetStatus(status: Boolean) {
        if (status)  //true -> internet available
        {
            binding.internetStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_wifi_connected
                )
            )
            binding.internetStatus.setColorFilter(ContextCompat.getColor(this, R.color.Green))
        } else {
            binding.internetStatus.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_wifi_disconnected
                )
            )
            binding.internetStatus.setColorFilter(ContextCompat.getColor(this, R.color.Red))
        }
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }
}