package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class RepresentativeViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val representatives = electionRepository.representatives

    val address = MutableLiveData(Address("", "", "", "", ""))

    private val _showErrorToast = MutableLiveData<String>()
    val showErrorToast: LiveData<String>
        get() = _showErrorToast

    fun refreshRepresentatives() {
        val addressString = address.value?.toFormattedString()
        if (!addressString.isNullOrEmpty())
            viewModelScope.launch {
                try {
                    electionRepository.refreshRepresentatives(addressString)
                } catch (e: Exception) {
                    Log.e("RepresentativeViewModel", e.message.toString())
                    _showErrorToast.value = e.message.toString()
                }
            }
    }

    fun showErrorToastComplete() {
        _showErrorToast.value = null
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
