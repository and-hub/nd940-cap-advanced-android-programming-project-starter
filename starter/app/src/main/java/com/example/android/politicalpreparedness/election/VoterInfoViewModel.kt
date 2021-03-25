package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val voterInfo = electionRepository.voterInfo

    private val _showToast = MutableLiveData<String>()
    val showToast: LiveData<String>
        get() = _showToast

    private val _openVotingLocations = MutableLiveData<String>()
    val openVotingLocations: LiveData<String>
        get() = _openVotingLocations

    private val _openBallotInfo = MutableLiveData<String>()
    val openBallotInfo: LiveData<String>
        get() = _openBallotInfo

    fun getVoterInfo(division: Division, electionId: Int) {
        val address = "${division.state}, ${division.country}"
        viewModelScope.launch {
            try {
                electionRepository.getVoterInfo(address, electionId)
            } catch (e: Exception) {
                Log.e("VoterInfoViewModel", e.message.toString())
                showToast("Error: ${e.message.toString()}")
            }
        }
    }

    fun showToast(message: String) {
        _showToast.value = message
    }

    fun showToastComplete() {
        _showToast.value = null
    }

    fun setVotingLocations(votingLocationsUrl: String?) {
        _openVotingLocations.value = votingLocationsUrl
    }

    fun openVotingLocationsComplete() {
        _openVotingLocations.value = null
    }

    fun setBallotInfo(ballotInfoUrl: String?) {
        _openBallotInfo.value = ballotInfoUrl
    }

    fun openBallotInfoComplete() {
        _openBallotInfo.value = null
    }

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}