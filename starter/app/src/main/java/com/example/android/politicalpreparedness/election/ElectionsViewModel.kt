package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class ElectionsViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val allElections = electionRepository.allElections

    val savedElections = electionRepository.savedElections

    init {
        viewModelScope.launch {
            try {
                electionRepository.refreshElections()
            } catch (e: Exception) {
                Log.e("ElectionsViewModel", e.message.toString())
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}