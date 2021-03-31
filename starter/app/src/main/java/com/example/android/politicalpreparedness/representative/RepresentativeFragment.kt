package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class RepresentativeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)

        val database = ElectionDatabase.getInstance(requireContext())
        val electionRepository = ElectionRepository(database)
        viewModel = ViewModelProvider(this, RepresentativeViewModelFactory(electionRepository)).get(RepresentativeViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.representativesRecycler.adapter = RepresentativeListAdapter()

        setupSpinner()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        viewModel.showToast.observe(viewLifecycleOwner) { resId ->
            resId?.let {
                Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
                viewModel.showToastComplete()
            }
        }

        viewModel.hideKeyboard.observe(viewLifecycleOwner) { hideKeyboard ->
            hideKeyboard?.let {
                if (hideKeyboard) {
                    hideKeyboard()
                    viewModel.hideKeyboardComplete()
                }
            }
        }

        viewModel.useMyLocation.observe(viewLifecycleOwner) { useMyLocation ->
            useMyLocation?.let {
                if (useMyLocation) {
                    useMyLocation()
                    viewModel.useMyLocationComplete()
                }
            }
        }

        return binding.root
    }

    private fun useMyLocation() {
        if (checkLocationPermissions())
            checkDeviceLocationSettingsAndGetLocation()
    }

    private fun checkDeviceLocationSettingsAndGetLocation(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                            requireActivity(),
                            REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("RepresentativeFragment", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                        binding.root,
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndGetLocation()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                checkDeviceLocationSettingsAndGetLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndGetLocation(false)
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val address = geoCodeLocation(it)
                viewModel.address.value = address
                val states = resources.getStringArray(R.array.states)
                binding.stateSpinner.setSelection(
                        if (states.contains(address.state))
                            states.indexOf(address.state)
                        else
                            0
                )
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare ?: "", address.subThoroughfare, address.locality
                            ?: "", address.adminArea ?: "", address.postalCode ?: "")
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.states,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.stateSpinner.adapter = adapter
        }

        binding.stateSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        viewModel.address.value?.state = parent.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}