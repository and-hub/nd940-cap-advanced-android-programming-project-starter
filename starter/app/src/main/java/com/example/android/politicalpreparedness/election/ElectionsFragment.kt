package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Division
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
            navToVoterInfo(it.id, it.division)
        })

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters

        return binding.root
    }

    private fun navToVoterInfo(electionId: Int, division: Division) {
        findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(electionId, division))
    }

    //TODO: Refresh adapters when fragment loads

}