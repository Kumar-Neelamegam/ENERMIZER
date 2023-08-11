package com.peter.enermizer.views.reports

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.peter.enermizer.R
import com.peter.enermizer.databinding.FragmentReportsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFragment : Fragment() {

    private var TAG = "ReportsFragment"
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    lateinit var reportsViewModel: ReportsViewModel

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
            val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault())
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            // Creating the date range string
            val selectedDateRange = "$startDateString - $endDateString"

            // Displaying the selected date range in the TextView
            binding.selectedDate.text = resources.getString(R.string.label_selected_date_range, selectedDateRange)
            reportsViewModel.getReportsBasedOnDates(startDateString, endDateString)
            updateReportsWithValues()
        }

        // Showing the date picker dialog
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun updateReportsWithValues() {
        reportsViewModel.observeResponseLiveData().observe(viewLifecycleOwner) { response ->
            Log.e(TAG, "Response from getReportsBasedOnDates")
            Log.e(TAG, response.toString())
        }
        //binding.valueReport1.text = "100"
        //binding.valueReport2.text = "200"
        //binding.valueReport3.text = "300"
        //binding.valueReport4.text = "500"
        //binding.valueReport5.text = "600"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}