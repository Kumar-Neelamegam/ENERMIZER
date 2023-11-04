package com.peter.enermizer.views.reports

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.datepicker.MaterialDatePicker
import com.peter.enermizer.R
import com.peter.enermizer.databinding.FragmentReportsBinding
import com.peter.enermizer.utils.DataStoreManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.squareup.picasso.Target

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
                if (customProgressDialog.isShowing) customProgressDialog.dismiss()
            }
        }
    }

    /**
     * Initialization of the views
     */
    private fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
            if (storedIpAddress?.isNotEmpty() == true) {

                val text = "Follow the link on browser $storedIpAddress/api/liveAutoModeStatus or click below to see 'Auto relay' status"

                val spannableString = SpannableString(text)

                // Create a clickable link
                val clickableLink = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        // Handle the click action, e.g., open a web page
                        // You can use an Intent to open a web browser with the URL.
                        val url = "http://$storedIpAddress/api/liveAutoModeStatus"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                }

                // Set the clickable link for the specified portion of text
                spannableString.setSpan(clickableLink, text.indexOf(storedIpAddress), text.indexOf("or "), 0)

                // Set the SpannableString to the TextView
                binding.autorelayInstruction.text = spannableString

                // Make sure the TextView is clickable and recognizes links
                binding.autorelayInstruction.movementMethod = LinkMovementMethod.getInstance()

            }
        }
    }

    /**
     * Controllisteners for the UI components
     */
    private fun controllisteners() {
        binding.datePicker.setOnClickListener {
            datePickerDialog()
        }

        binding.autoRelay.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
                if (storedIpAddress?.isNotEmpty() == true) {
                    reportsViewModel.getAutoRelayStatus(storedIpAddress)
                }
            }

            //Show progress
            val title = "Please wait"
            val message = "Fetching auto mode - relay status..."
            showProgress(title, message)

            reportsViewModel.observeResponseLiveData2().observe(viewLifecycleOwner) { response ->
                //Hide progress
                customProgressDialog.dismiss()
                showPopupWithImage(response.byteStream())
            }
        }
    }

    private fun showPopupWithImage(inputStream: InputStream) {
        activity?.runOnUiThread {
            // Create a Bitmap from the InputStream and set it to the ImageView
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if(bitmap!=null) {
                val customPopupDialog = CustomPopupDialogFragment(bitmap)
                customPopupDialog.show(childFragmentManager, "relay")
            }
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
            binding.selectedDate.text =
                resources.getString(R.string.label_selected_date_range, selectedDateRange)
            CoroutineScope(Dispatchers.IO).launch {
                val storedIpAddress = dataStoreManager.settingsIPAddressFlow()
                if (storedIpAddress?.isNotEmpty() == true) {
                    reportsViewModel.getReportsBasedOnDates(
                        storedIpAddress.toString(),
                        startDateString,
                        endDateString
                    )
                }
            }

            //Show progress
            val title = "Please wait"
            val message = "Fetching reports for - "+ binding.selectedDate.text.toString()

            showProgress(title, message)
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
        return if (reportValue == "nan") {
            0.0
        } else {
            decimalFormat.format(reportValue.toDouble())
        }
    }

    fun showProgress(title:String, message: String) {
        // Initialize the custom progress dialog
        customProgressDialog = Dialog(requireContext())
        customProgressDialog.setContentView(R.layout.custom_progress_dialog_layout)
        customProgressDialog.setCancelable(false)

        // Find the views in the custom dialog layout
        val dialogTitle = customProgressDialog.findViewById<TextView>(R.id.dialogTitle)
        val dialogText = customProgressDialog.findViewById<TextView>(R.id.dialogText)
        val progressBar = customProgressDialog.findViewById<ProgressBar>(R.id.dialogProgressBar)

        // Set title and text
        dialogTitle.text = title
        dialogText.text = message

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