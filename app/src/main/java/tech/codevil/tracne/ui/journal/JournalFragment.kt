package tech.codevil.tracne.ui.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.databinding.FragmentJournalBinding
import tech.codevil.tracne.model.Entry

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding: FragmentJournalBinding get() = _binding!!

    private val journalViewModel: JournalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        journalViewModel.date.observe(viewLifecycleOwner, {
            binding.dateJournal.text = it
        })
        journalViewModel.insertSuccess.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Error -> {
                    binding.submitButtonJournal.isEnabled = true
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataState.Success -> {
                    Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(JournalFragmentDirections.actionJournalFragmentToHomeFragment())
                }
                is DataState.Loading -> {
                    binding.submitButtonJournal.isEnabled = false
                }
            }
        })

        binding.submitButtonJournal.setOnClickListener {
            journalViewModel.insertEntry(
                Entry(
                    timestamp = System.currentTimeMillis(),
                    mood = binding.moodBarJournal.value,
                    sleep = binding.sleepBarJournal.value,
                    newSpots = binding.spotsBarJournal.value,
                    rating = binding.rateBarJournal.value,
                    templateValues = mapOf()
                )
            )
        }

        binding.customizeImageViewJournal.setOnClickListener {
            findNavController().navigate(JournalFragmentDirections.actionJournalFragmentToTemplatesFragment())
        }
    }

}