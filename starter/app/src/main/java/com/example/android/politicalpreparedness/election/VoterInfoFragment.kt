package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus
import com.example.android.politicalpreparedness.utils.ElectionFollowingStatus.*
import com.example.android.politicalpreparedness.utils.toFormattedString

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentVoterInfoBinding.inflate(inflater)

        val database = ElectionDatabase.getInstance(requireContext())
        val electionRepository = ElectionRepository(database)
        viewModel = ViewModelProvider(this, VoterInfoViewModelFactory(electionRepository)).get(VoterInfoViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        viewModel.getVoterInfo(division, electionId)
        viewModel.getCurrentFollowingStatus(electionId)

        viewModel.voterInfo.observe(viewLifecycleOwner) {
            it?.let {
                setViewsVisibilityAndContent(it)
            }
        }

        viewModel.showToast.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.showToastComplete()
            }
        }

        viewModel.openVotingLocations.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                openWebPage(url)
                viewModel.openVotingLocationsComplete()
            }
        }

        viewModel.openBallotInfo.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                openWebPage(url)
                viewModel.openBallotInfoComplete()
            }
        }

        viewModel.electionFollowingStatus.observe(viewLifecycleOwner) {
            when (it) {
                FOLLOWED -> {
                    binding.followButton.text = getString(R.string.unfollow_election)
                    viewModel.resetFollowingStatus()
                }
                UNFOLLOWED -> {
                    binding.followButton.text = getString(R.string.follow_election)
                    viewModel.resetFollowingStatus()
                }
                ERROR -> {
                    viewModel.showToast(getString(R.string.following_election_failed))
                    viewModel.resetFollowingStatus()
                }
                else -> {
                }
            }
        }

        return binding.root
    }

    private fun setViewsVisibilityAndContent(voterInfo: VoterInfoResponse) {
        binding.electionName.title = voterInfo.election.name
        binding.electionDate.text = voterInfo.election.electionDay.toFormattedString()

        val votingLocationsUrl = voterInfo.state?.first()?.electionAdministrationBody?.votingLocationFinderUrl
        binding.stateLocations.visibility = if (votingLocationsUrl.isNullOrEmpty()) GONE else VISIBLE

        val ballotInfoUrl = voterInfo.state?.first()?.electionAdministrationBody?.ballotInfoUrl
        binding.stateBallot.visibility = if (ballotInfoUrl.isNullOrEmpty()) GONE else VISIBLE

        binding.stateHeader.visibility =
                if (binding.stateLocations.visibility == GONE &&
                        binding.stateBallot.visibility == GONE) GONE
                else VISIBLE

        val address = voterInfo.state?.first()?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()
        if (address != null) {
            binding.address.text = address
            binding.addressGroup.visibility = VISIBLE
        } else {
            binding.addressGroup.visibility = GONE
        }
    }

    private fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}