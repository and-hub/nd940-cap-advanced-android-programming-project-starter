package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(elections: List<Election>)

    @Query("SELECT * FROM election_table ORDER BY electionDay")
    fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE isSaved = 1 ORDER BY electionDay")
    fun getSavedElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id")
    fun getElection(id: Int): Election

    @Delete
    fun delete(election: Election)

    @Query("DELETE FROM election_table WHERE isSaved = 0")
    fun deleteUnsavedElections()

    @Query("DELETE FROM election_table")
    fun clear()
}