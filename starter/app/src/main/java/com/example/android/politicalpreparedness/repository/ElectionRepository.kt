package com.example.android.politicalpreparedness.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.models.FollowElection
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ElectionRepository(private val database: ElectionDatabase) {

    val allElections: LiveData<List<Election>> = database.electionDao.getAllElections()
    val savedElections: LiveData<List<Election>> = database.electionDao.getSavedElections()

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

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

    suspend fun getFollowingStatus(electionId: Int): ElectionFollowingStatus = withContext(Dispatchers.IO) {
        try {
            return@withContext if (database.electionDao.getElection(electionId).isFollowed)
                FOLLOWED
            else
                UNFOLLOWED
        } catch (e: Exception) {
            Log.e("ElectionRepository", e.message, e)
            return@withContext ERROR
        }
    }

    suspend fun toggleElectionFollowed(electionId: Int): ElectionFollowingStatus =
            withContext(Dispatchers.IO) {
                try {
                    val wasElectionFollowed = database.electionDao.getElection(electionId).isFollowed
                    val followElection = FollowElection(electionId, !wasElectionFollowed)
                    return@withContext if (database.electionDao.setElectionFollowed(followElection) == 1) {
                        if (wasElectionFollowed)
                            UNFOLLOWED
                        else
                            FOLLOWED
                    } else
                        ERROR
                } catch (e: Exception) {
                    Log.e("ElectionRepository", e.message, e)
                    return@withContext ERROR
                }
            }

    suspend fun refreshRepresentatives(address: String) {
        withContext(Dispatchers.IO) {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentativeResponse(address)
            _representatives.postValue(offices.flatMap { office -> office.getRepresentatives(officials) })
        }
    }

}