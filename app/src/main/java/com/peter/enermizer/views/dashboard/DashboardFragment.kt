package com.peter.enermizer.views.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.peter.enermizer.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        /* val textView: TextView = binding.textDashboard
         dashboardViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/
        initialize()
        controllerListeners()
        listenForErrors()
        return root
    }

    private fun controllerListeners() {
        binding.btnOn1.setOnClickListener {
            controlSockets(1, 1)
        }

        binding.btnOff1.setOnClickListener {
            controlSockets(1, 0)
        }

        binding.btnOn2.setOnClickListener {
            controlSockets(2, 1)
        }

        binding.btnOff2.setOnClickListener {
            controlSockets(2, 0)
        }

    }

    private fun initialize() {
//        dashboardViewModel.checkSocketController(1)
//        dashboardViewModel.checkSocketController(2)
//        changeSocketStatus()
    }

    fun changeSocketStatus() {
        dashboardViewModel.observeSocketStatus().observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "Response from Socket Status")
            Log.e(TAG, response.toString())
        }
    }

    private fun listenForErrors() {
        dashboardViewModel.errorStatus.observeForever {
            if (it.status) {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun controlSockets(switchNumber: Int, switchStatus: Int) {
        dashboardViewModel.callSocketController(switchNumber, switchStatus)
        dashboardViewModel.observeResponseLiveData()
            .observe(viewLifecycleOwner) { response ->
                Log.e(TAG, "Response from Socket Controller")
                Log.e(TAG, response.toString())
            }
    }

}