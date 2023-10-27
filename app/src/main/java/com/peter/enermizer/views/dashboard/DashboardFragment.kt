package com.peter.enermizer.views.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.peter.enermizer.R
import com.peter.enermizer.databinding.FragmentDashboardBinding
import com.peter.enermizer.utils.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel
    private var TAG = "DashboardFragment"
    private val relay1Number = 1
    private val relay2Number = 2
    private var ipaddress = ""
    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initialize()
        controllerListeners()
        listenForErrors()
        return root
    }

    private fun controllerListeners() {
        /**
         * Definition for Relay mode
         * ON MODE = 1 = RELAY STATUS
         * OFF MODE = 0 = RELAY STATUS
         * AUTO MODE = 2 = RELAY STATUS
         */
        binding.btnOn1.setOnClickListener {
            relayController(relay1Number, 1) // TURN ON RELAY 1
        }

        binding.btnOff1.setOnClickListener {
            relayController(relay1Number, 0) // TURN OFF RELAY 1
        }

        binding.btnOn2.setOnClickListener {
            relayController(relay2Number, 1) // TURN ON RELAY 2
        }

        binding.btnOff2.setOnClickListener {
            relayController(relay2Number, 0) // TURN OFF RELAY 2
        }

        binding.btnRelay1Auto.setOnClickListener {
            relayController(relay1Number, 2) // TURN AUTO RELAY 1
        }

        binding.btnRelay2Auto.setOnClickListener {
            relayController(relay2Number, 2) // TURN AUTO RELAY 2
        }
    }

    private fun initialize() {
        updateRelayStatus()
    }

    private fun updateRelayStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
            if (storedIpAddress?.isNotEmpty() == true) {
                ipaddress = storedIpAddress.toString()
                dashboardViewModel.liveCheckRelayControllers(ipaddress, relay1Number, relay2Number)
                changeRelayStatus()
            }
        }
    }

    private fun changeRelayStatus() {
        dashboardViewModel.observeRelayStatus().observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "Response --> Relay Status")
            Log.e(TAG, response.toString())
            if (response.relay == relay1Number) {
                when (response.relaystatus) {
                    "True" -> {
                        binding.imgvwRelay1.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.Green
                            )
                        )
                    }
                    "False" -> {
                        binding.imgvwRelay1.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.Red
                            )
                        )
                    }
                    "Auto" -> {
                        binding.imgvwRelay1.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.Orange
                            )
                        )
                    }
                }
            } else if (response.relay == relay2Number) {
                when (response.relaystatus) {
                    "True" -> {
                        binding.imgvwRelay2.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.Green
                            )
                        )
                    }
                    "False" -> {
                        binding.imgvwRelay2.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.Red
                            )
                        )
                    }
                    "Auto" -> {
                        binding.imgvwRelay2.setColorFilter(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color.Orange
                            )
                        )
                    }
                }
            }

        }
    }

    private fun listenForErrors() {
        dashboardViewModel.errorStatus.observeForever {
            if (it.status) {
                //Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                Log.e(TAG, it.message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun relayController(relayNumber: Int, relayStatus: Int) {
        dashboardViewModel.callRelayController(ipaddress, relayNumber, relayStatus)
        dashboardViewModel.observeResponseLiveData()
            .observe(viewLifecycleOwner) { response ->
                Log.e(TAG, "Response --> Relay Controller")
                Log.e(TAG, response.toString())
                updateRelayStatus()
                if (response.status == true) {
                    Toast.makeText(activity, "Updated the relay status...", Toast.LENGTH_LONG).show()
                }
            }
    }

}