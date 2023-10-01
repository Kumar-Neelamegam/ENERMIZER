package com.peter.enermizer.views.reports

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.peter.enermizer.R
import com.peter.enermizer.databinding.FragmentReportsBinding
import com.peter.enermizer.utils.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFragment : Fragment() {

    private var TAG = "ReportsFragment"
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    lateinit var reportsViewModel: ReportsViewModel
    private lateinit var customProgressDialog: Dialog
    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reportsViewModel =
            ViewModelProvider(this)[ReportsViewModel::class.java]

        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        init()
        controllisteners()
        listenForErrors()

        return root
    }

    private fun listenForErrors() {
        reportsViewModel.errorStatus.observeForever {
            if (it.status) {
                Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
                if(customProgressDialog.isShowing) customProgressDialog.dismiss()
            }
        }
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
        binding.datePicker.setOnClickListener {
            datePickerDialog()
        }
    }

    private fun datePickerDialog() {
        // Creating a MaterialDatePicker builder for selecting a date range
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select a date range")

        // Building the date picker dialog
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Retrieving the selected start and end dates
            val startDate = selection.first
            val endDate = selection.second

            // Formatting the selected dates as strings
            // 2023-07-25 15:00:00
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            // Creating the date range string
            val selectedDateRange = "$startDateString - $endDateString"

            // Displaying the selected date range in the TextView
            binding.selectedDate.text = resources.getString(R.string.label_selected_date_range, selectedDateRange)
            CoroutineScope(Dispatchers.IO).launch {
                val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
                if(storedIpAddress?.isNotEmpty() == true) {
                    reportsViewModel.getReportsBasedOnDates(storedIpAddress.toString(), startDateString, endDateString)
                }
            }

            //Show progress
            showProgress(binding.selectedDate.text.toString())
            updateReportsWithValues()
        }

        // Showing the date picker dialog
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun updateReportsWithValues() {
        reportsViewModel.observeResponseLiveData().observe(viewLifecycleOwner) { response ->
            //Hide progress
            customProgressDialog.dismiss()
            Log.e(TAG, "Response from getReportsBasedOnDates")
            Log.e(TAG, response.toString())

            val jsonObject = response.message?.let { JSONObject(it) }

            val report1Value = jsonObject?.getString("report1")
                ?.let { checkNan(it) }
            val report2Value = jsonObject?.getString("report2")
                ?.let { checkNan(it) }
            val report3Value = jsonObject?.getString("report3")
                ?.let { checkNan(it) }
            val report4Value = jsonObject?.getString("report4")
                ?.let { checkNan(it) }
            val report5Value = jsonObject?.getString("report5")
                ?.let { checkNan(it) }

            binding.valueReport1.text = report1Value.toString()
            binding.valueReport2.text = report2Value.toString()
            binding.valueReport3.text = report3Value.toString()
            binding.valueReport4.text = report4Value.toString()
            binding.valueReport5.text = report5Value.toString()
        }

    }

    fun checkNan(reportValue: String): Any {
        val decimalFormat = DecimalFormat("#.####")
        decimalFormat.roundingMode = RoundingMode.HALF_UP
        return if(reportValue == "nan") {
            0.0
        } else {
            decimalFormat.format(reportValue.toDouble())
        }
    }

    fun showProgress(selectedDates: String) {
        // Initialize the custom progress dialog
        customProgressDialog = Dialog(requireContext())
        customProgressDialog.setContentView(R.layout.custom_progress_dialog_layout)
        customProgressDialog.setCancelable(false)

        // Find the views in the custom dialog layout
        val dialogTitle = customProgressDialog.findViewById<TextView>(R.id.dialogTitle)
        val dialogText = customProgressDialog.findViewById<TextView>(R.id.dialogText)
        val progressBar = customProgressDialog.findViewById<ProgressBar>(R.id.dialogProgressBar)

        // Set title and text
        dialogTitle.text = "Please wait"
        dialogText.text = "Fetching reports for - $selectedDates"

        // Show the custom progress dialog
        customProgressDialog.show()
/*

        Handler(Looper.getMainLooper()).postDelayed({
            // Dismiss the custom progress dialog when loading is done
            customProgressDialog.dismiss()
        }, 15000)
*/

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}