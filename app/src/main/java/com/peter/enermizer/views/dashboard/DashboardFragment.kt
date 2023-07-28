package com.peter.enermizer.views.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

        return root
    }

    private fun controllerListeners() {
        binding.btnOn.setOnClickListener {
            tempCallToBulbOn()
        }

        binding.btnOff.setOnClickListener {
            tempCallToBulbOff()
        }
    }

    private fun initialize() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun tempCallToBulbOn() {
        dashboardViewModel.callBulbOnService()
        dashboardViewModel.observeResponseLiveData().observe(viewLifecycleOwner, Observer { response ->
            Log.e(TAG, "Response from BulbOn")
            Log.e(TAG, response.toString())
            Log.e(TAG, response.toString())
        })
    }
    fun tempCallToBulbOff() {
        dashboardViewModel.callBulbOffService()
        dashboardViewModel.observeResponseLiveData().observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "Response from BulbOff")
            Log.e(TAG, response.toString())
            Log.e(TAG, response.toString())
        }
    }

}