package com.peluso.walletguru.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.peluso.walletguru.LOCATION_PREF_KEY
import com.peluso.walletguru.R
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState

private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class SettingsFragment : Fragment() {

    private var location: String? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var locationSwitch: SwitchCompat

    // permissions
    private val isLocationPermissionGranted
        get() = requireContext().hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        requireActivity().runOnUiThread {
            val builder = AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission required")
                    .setMessage("Need location")
                    .setPositiveButton("OK") { _, _ ->
                        requireActivity().requestPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                LOCATION_PERMISSION_REQUEST_CODE
                        )
                    }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    Log.i("SettingsFragment", "Permissions detected")
                    viewModel.enableLocation(requireContext())
                }
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel =
                ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationSwitch = view.findViewById(R.id.location_switch)
        locationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (!isLocationPermissionGranted) {
                    requestLocationPermission()
                } else {
                    if (location == null) {
                        viewModel.enableLocation(requireContext())
                    }
                }
            } else {
                viewModel.disableLocation()
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            handleViewState(it)
        })
        isLocationInSharedPrefs()
    }

    private fun handleViewState(state: MainViewState) {
        locationSwitch.isChecked = state.locationEnabled || isLocationInSharedPrefs()
    }

    private fun isLocationInSharedPrefs(): Boolean {
        // set the check based on if we have the location in shared prefs
        requireActivity().getPreferences(Context.MODE_PRIVATE).getString(LOCATION_PREF_KEY, null)?.let {
            location = it
            locationSwitch.isChecked = true
            return true
        } ?: kotlin.run {
            return false
        }
    }
}

// ext functions
fun Context.hasPermission(permissionType: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permissionType) ==
            PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermission(permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
}