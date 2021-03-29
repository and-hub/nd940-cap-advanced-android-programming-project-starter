package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus.ERROR
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus.LOADING
import com.example.android.politicalpreparedness.utils.LoadingStatus
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val voterInfo = electionRepository.voterInfo

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    private val _electionFollowingStatus = MutableLiveData<ElectionFollowingStatus>()
    val electionFollowingStatus: LiveData<ElectionFollowingStatus>
        get() = _electionFollowingStatus

    private val _showToast = MutableLiveData<Int>()
    val showToast: LiveData<Int>
        get() = _showToast

    private val _openVotingLocations = MutableLiveData<String>()
    val openVotingLocations: LiveData<String>
        get() = _openVotingLocations

    private val _openBallotInfo = MutableLiveData<String>()
    val openBallotInfo: LiveData<String>
        get() = _openBallotInfo

    fun getVoterInfo(division: Division, electionId: Int) {
        _loadingStatus.value = LoadingStatus.LOADING
        val address = "${division.state}, ${division.country}"
        viewModelScope.launch {
            try {
                electionRepository.getVoterInfo(address, electionId)
                _loadingStatus.value = LoadingStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("VoterInfoViewModel", e.message, e)
                showToast(R.string.error_loading_voter_info)
                _loadingStatus.value = LoadingStatus.ERROR
            }
        }
    }

    fun getCurrentFollowingStatus(electionId: Int) {
        viewModelScope.launch {
            _electionFollowingStatus.value = electionRepository.getFollowingStatus(electionId)
        }
    }

    fun showToast(resId: Int) {
        _showToast.value = resId
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