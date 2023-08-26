package com.peter.enermizer.views.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.peter.enermizer.databinding.FragmentSettingsBinding

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}