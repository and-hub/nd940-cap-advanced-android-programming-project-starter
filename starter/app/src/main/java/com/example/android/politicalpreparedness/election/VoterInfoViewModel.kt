package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(private val electionRepository: ElectionRepository) : ViewModel() {

    val voterInfo = electionRepository.voterInfo

    fun getVoterInfo(division: Division, electionId: Int) {
        val address = "${division.state}, ${division.country}"
        viewModelScope.launch {
            try {
                electionRepository.getVoterInfo(address, electionId)
            } catch (e: Exception) {
                Log.e("VoterInfoViewModel", e.message.toString())
            }
        }
    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}