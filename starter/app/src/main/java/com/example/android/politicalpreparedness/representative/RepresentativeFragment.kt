package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import java.util.Locale

class RepresentativeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        //TODO: Add Constant for Location request
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var viewModel: RepresentativeViewModel

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

                //TODO: Establish bindings

                //TODO: Establish button listeners for field and location search

        viewModel.showErrorToast.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(
                        context,
                        getString(R.string.loading_error_message_format, getString(R.string.representatives), it),
                        Toast.LENGTH_LONG
                )
                        .show()
                viewModel.showErrorToastComplete()
            }
        }

        return binding.root
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.states,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.state.adapter = adapter
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
    }

//    private fun checkLocationPermissions(): Boolean {
//        return if (isPermissionGranted()) {
//            true
//        } else {
//            //TODO: Request Location permissions
//            false
//        }
//    }

//    private fun isPermissionGranted() : Boolean {
//        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
//    }

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        viewModel.address.value?.state = parent.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

}