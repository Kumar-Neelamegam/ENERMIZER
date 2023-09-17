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

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel
    private var TAG = "DashboardFragment"

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
        //TODO check whether ip address and relay power are configured in settings
        binding.btnOn1.setOnClickListener {
            relayController(1, 1) // TURN ON RELAY 1
        }

        binding.btnOff1.setOnClickListener {
            relayController(1, 0) // TURN OFF RELAY 1
        }

        binding.btnOn2.setOnClickListener {
            relayController(2, 1) // TURN ON RELAY 2
        }

        binding.btnOff2.setOnClickListener {
            relayController(2, 0) // TURN OFF RELAY 2
        }

    }

    private fun initialize() {
        updateRelayStatus()
    }

    private fun updateRelayStatus() {
        dashboardViewModel.checkRelayController(1)
        dashboardViewModel.checkRelayController(2)
        changeRelayStatus()
    }

    private fun changeRelayStatus() {
        dashboardViewModel.observeRelayStatus().observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "Response --> Relay Status")
            Log.e(TAG, response.toString())
            if (response.relay == 1) {
                if (response.status == true) {
                    binding.imgvwRelay1.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.Green
                        )
                    )
                } else {
                    binding.imgvwRelay1.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.Red
                        )
                    )
                }
            } else if (response.relay == 2) {
                if (response.status == true) {
                    binding.imgvwRelay2.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.Green
                        )
                    )
                } else {
                    binding.imgvwRelay2.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.Red
                        )
                    )
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
        dashboardViewModel.callRelayController(relayNumber, relayStatus)
        dashboardViewModel.observeResponseLiveData()
            .observe(viewLifecycleOwner) { response ->
                Log.e(TAG, "Response --> Relay Controller")
                Log.e(TAG, response.toString())
                updateRelayStatus()

                if (response.status == true) {
                }
            }
    }

}