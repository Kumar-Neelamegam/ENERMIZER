package com.peter.enermizer.views.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.peter.enermizer.R
import com.peter.enermizer.databinding.FragmentReportsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        controllisteners()

        return root
    }

    private fun controllisteners() {
        binding.datePicker.setOnClickListener {
            datePickerdialog()
        }
    }

    private fun datePickerdialog() {
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
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            // Creating the date range string
            val selectedDateRange = "$startDateString - $endDateString"

            // Displaying the selected date range in the TextView
            binding.selectedDate.text = resources.getString(R.string.label_selected_date_range, selectedDateRange)

            updateReportsWithValues(reportsViewModel.getReportsBasedOnDates(startDateString, endDateString))
        }

        // Showing the date picker dialog
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun updateReportsWithValues(reportsBasedOnDates: List<String>) {
        binding.valueReport1.text = "100"
        binding.valueReport2.text = "200"
        binding.valueReport3.text = "300"
        binding.valueReport4.text = "500"
        binding.valueReport5.text = "600"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}