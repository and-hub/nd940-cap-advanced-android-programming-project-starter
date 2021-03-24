package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class ElectionRepository(private val database: ElectionDatabase) {

    val allElections: LiveData<List<Election>> = database.electionDao.getAllElections()
    val savedElections: LiveData<List<Election>> = database.electionDao.getSavedElections()

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            val elections = CivicsApi.retrofitService.getElectionResponse().elections
            database.electionDao.deleteUnsavedElections()
            database.electionDao.insertAll(elections)
        }
    }

    suspend fun getVoterInfo(address: String, electionId: Int) {
        withContext(Dispatchers.IO) {
            _voterInfo.postValue(CivicsApi.retrofitService.getVoterInfoResponse(address, electionId))
        }
    }
}