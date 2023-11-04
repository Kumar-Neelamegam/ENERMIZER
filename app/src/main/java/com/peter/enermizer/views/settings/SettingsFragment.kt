package com.peter.enermizer.views.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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

    private lateinit var settingsViewModel: SettingsViewModel

    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        controllisteners()
        listenForErrors()
        return root
    }

    private fun listenForErrors() {

        settingsViewModel.errorStatus.observe(viewLifecycleOwner)  {
            if (it.status) {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Initialization of the views
     */
    private fun init() {
        loadSettings()
    }

    private fun loadSettings() {

        CoroutineScope(Dispatchers.Main).launch {
            val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
            if (storedIpAddress?.isNotEmpty() == true) {
                binding.editIpaddress.setText(dataStoreManager.settingsIPAddressFlow().toString())
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
            if (storedIpAddress?.isNotEmpty() == true) {
                binding.editRelay1Power.setText(dataStoreManager.settingsRelay1Power().toString())
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
            if (storedIpAddress?.isNotEmpty() == true) {
                binding.editRelay2Power.setText(dataStoreManager.settingsRelay2Power().toString())
            }
        }

    }

    /**
     * Controllisteners for the UI components
     */
    private fun controllisteners() {
        binding.saveButton.setOnClickListener {
            if (binding.editIpaddress.text.isNullOrEmpty()) {
                showErrorDialog("Enter the IP address with port")
                return@setOnClickListener
            }
            if (binding.editRelay1Power.text.isNullOrEmpty()) {
                showErrorDialog("Enter the relay 1 power in kWh")
                return@setOnClickListener
            }
            if (binding.editRelay2Power.text.isNullOrEmpty()) {
                showErrorDialog("Enter the relay 1 power in kWh")
                return@setOnClickListener
            }

            val ipaddress = binding.editIpaddress.text.toString()
            val relay1Power = Integer.parseInt(binding.editRelay1Power.text.toString())
            val relay2Power = Integer.parseInt(binding.editRelay2Power.text.toString())

            CoroutineScope(Dispatchers.IO).launch {
                DataStoreManager(requireContext()).storeSettingsInfo(
                    ipaddress,
                    relay1Power,
                    relay2Power
                )
            }
            showSuccessDialog("Saved successfully..")

        }

        binding.editRelay2Power.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Perform the button click event
                binding.saveButton.performClick()
                return@setOnEditorActionListener true
            }
            false
        };

    }

    private fun showErrorDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .show()
    }

    private fun showSuccessDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success")
            .setContentText(message)
            .show()
        // TODO save the settings to the server
        CoroutineScope(Dispatchers.IO).launch {
            settingsViewModel.postRelaySettings(
                binding.editIpaddress.text.toString(),
                binding.editRelay1Power.text.toString(),
                binding.editRelay2Power.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}