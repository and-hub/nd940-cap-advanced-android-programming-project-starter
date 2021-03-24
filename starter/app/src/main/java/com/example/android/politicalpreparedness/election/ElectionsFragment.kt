package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.repository.ElectionRepository

class ElectionsFragment : Fragment() {

    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentElectionBinding.inflate(inflater)

        val database = ElectionDatabase.getInstance(requireContext())
        val electionRepository = ElectionRepository(database)
        viewModel = ViewModelProvider(this, ElectionsViewModelFactory(electionRepository)).get(ElectionsViewModel::class.java)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.upcomingElectionsRecycler.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            //TODO: navigate to voterInfoFragment
        })

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}