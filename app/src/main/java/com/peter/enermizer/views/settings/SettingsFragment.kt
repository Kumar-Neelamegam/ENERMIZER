package com.peter.enermizer.views.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.peter.enermizer.databinding.FragmentSettingsBinding
import com.peter.enermizer.utils.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        controllisteners()
        return root
    }

    /**
     * Initialization of the views
     */
    private fun init() {

    }

    /**
     * Controllisteners for the UI components
     */
    private fun controllisteners() {
            binding.saveButton.setOnClickListener {
                if(binding.editIpaddress.text.isNullOrEmpty()) {
                    showErrorDialog("Enter the IP address with port")
                    return@setOnClickListener
                }
                if(binding.editRelay1Power.text.isNullOrEmpty()) {
                    showErrorDialog("Enter the relay 1 power in kWh")
                    return@setOnClickListener
                }
                if(binding.editRelay2Power.text.isNullOrEmpty()) {
                    showErrorDialog("Enter the relay 1 power in kWh")
                    return@setOnClickListener
                }

                val ipaddress = binding.editIpaddress.text.toString()
                val relay1Power = Integer.parseInt(binding.editRelay1Power.text.toString())
                val relay2Power = Integer.parseInt(binding.editRelay2Power.text.toString())

                CoroutineScope(Dispatchers.IO).launch {
                    DataStoreManager(requireContext()).storeSettingsInfo(ipaddress, relay1Power, relay2Power)
                }
            }

    }

    private fun showErrorDialog(message:String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}