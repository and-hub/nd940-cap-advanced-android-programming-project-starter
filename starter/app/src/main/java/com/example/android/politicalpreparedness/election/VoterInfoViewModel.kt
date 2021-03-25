package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus.ERROR
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus.LOADING
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val voterInfo = electionRepository.voterInfo

    private val _electionFollowingStatus = MutableLiveData<ElectionFollowingStatus>()
    val electionFollowingStatus: LiveData<ElectionFollowingStatus>
        get() = _electionFollowingStatus

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

    fun getCurrentFollowingStatus(electionId: Int) {
        viewModelScope.launch {
            _electionFollowingStatus.value = electionRepository.getFollowingStatus(electionId)
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

    fun toggleElectionFollowed() {
        if (_electionFollowingStatus.value != LOADING) {
            _electionFollowingStatus.value = LOADING
            val electionId = voterInfo.value?.election?.id
            if (electionId != null) {
                viewModelScope.launch {
                    _electionFollowingStatus.value = electionRepository.toggleElectionFollowed(electionId)
                }
            } else {
                _electionFollowingStatus.value = ERROR
            }
        }
    }

    fun resetFollowingStatus() {
        _electionFollowingStatus.value = null
    }
}