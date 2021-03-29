package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.LoadingStatus
import kotlinx.coroutines.launch
import java.lang.Exception

class ElectionsViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val allElections = electionRepository.allElections

    val savedElections = electionRepository.savedElections

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    init {
        _loadingStatus.value = LoadingStatus.LOADING
        viewModelScope.launch {
            try {
                electionRepository.refreshElections()
                _loadingStatus.value = LoadingStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("ElectionsViewModel", e.message, e)
                _loadingStatus.value = LoadingStatus.ERROR
            }
        }
    }
}