package com.peter.enermizer.views.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import cn.pedant.SweetAlert.SweetAlertDialog
import com.peter.enermizer.databinding.FragmentSettingsBinding
import com.peter.enermizer.utils.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.first
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
        loadSettings()
    }

    private fun loadSettings() {
        if(DataStoreManager(requireContext()).settingsIPAddressFlow.toString().isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = DataStoreManager(requireContext()).settingsIPAddressFlow.first()
                binding.editIpaddress.setText(data.toString())
            }
        }

        if(DataStoreManager(requireContext()).settingsRelay1Power.toString().isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = DataStoreManager(requireContext()).settingsRelay1Power.first()
                binding.editRelay1Power.setText(data.toString())
            }
        }

        if(DataStoreManager(requireContext()).settingsRelay2Power.toString().isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val data = DataStoreManager(requireContext()).settingsRelay2Power.first()
                binding.editRelay2Power.setText(data.toString())
            }
        }
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
                showSuccessDialog("Saved successfully..")

            }

    }

    private fun showErrorDialog(message:String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .show()
    }

    private fun showSuccessDialog(message:String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success")
            .setContentText(message)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}