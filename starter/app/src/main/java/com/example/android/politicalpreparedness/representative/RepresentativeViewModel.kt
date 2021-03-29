package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val representatives = electionRepository.representatives

    val address = MutableLiveData(Address("", "", "", "", ""))

    private val _showToast = MutableLiveData<Int>()
    val showToast: LiveData<Int>
        get() = _showToast

    private val _useMyLocation = MutableLiveData<Boolean>()
    val useMyLocation: LiveData<Boolean>
        get() = _useMyLocation

    fun refreshRepresentatives() {
        val addressString = address.value?.toFormattedString()
        if (!addressString.isNullOrEmpty())
            viewModelScope.launch {
                try {
                    electionRepository.refreshRepresentatives(addressString)
                } catch (e: Exception) {
                    Log.e("RepresentativeViewModel", e.message.toString())
                    _showToast.value = R.string.error_loading_representatives
                }
            }
        else
            _showToast.value = R.string.address_cant_be_empty
    }

    fun showToastComplete() {
        _showToast.value = null
    }

    fun useMyLocation() {
        _useMyLocation.value = true
    }

    fun useMyLocationComplete() {
        _useMyLocation.value = null
    }
    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
