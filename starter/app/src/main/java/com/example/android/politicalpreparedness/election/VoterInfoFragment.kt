package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.toFormattedString

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentVoterInfoBinding.inflate(inflater)

        val database = ElectionDatabase.getInstance(requireContext())
        val electionRepository = ElectionRepository(database)
        viewModel = ViewModelProvider(this, VoterInfoViewModelFactory(electionRepository)).get(VoterInfoViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        viewModel.getVoterInfo(division, electionId)

        viewModel.voterInfo.observe(viewLifecycleOwner) {
            it?.let {
                binding.electionName.title = it.election.name
                binding.electionDate.text = it.election.electionDay.toFormattedString()
                val address = it.state?.first()?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
                if (address != null) {
                    binding.address.text = address
                    binding.addressGroup.visibility = VISIBLE
                } else {
                    binding.addressGroup.visibility = GONE
                }
            }
        }
        //TODO: Add binding values

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root
    }

    //TODO: Create method to load URL intents

}