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
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.databinding.FragmentJournalBinding
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.ui.common.SliderTemplateView
import tech.codevil.tracne.ui.common.TemplateViewCallback
import tech.codevil.tracne.ui.common.YesNoTemplateView

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class JournalFragment : Fragment(), TemplateViewCallback {

    private var _binding: FragmentJournalBinding? = null
    private val binding: FragmentJournalBinding get() = _binding!!

    private val journalViewModel: JournalViewModel by viewModels()
    private val templateValues: MutableMap<String, Int> = mutableMapOf()

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
        journalViewModel.date.observe(viewLifecycleOwner) {
            binding.dateJournal.text = it
        }
        journalViewModel.insertSuccess.observe(viewLifecycleOwner) {
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
        }
        journalViewModel.templates.observe(viewLifecycleOwner) { it ->
            binding.templatesContainerJournal.removeAllViews()
            it.map {
                when(it.type) {
                    Constants.QUESTION_TYPE_YES_NO -> {
                        val yesNoView = YesNoTemplateView(requireContext())
                        binding.templatesContainerJournal.addView(yesNoView)
                        yesNoView.setTemplate(it)
                        yesNoView.setCallback(this)
                    }
                    Constants.QUESTION_TYPE_SLIDER -> {
                        val sliderView = SliderTemplateView(requireContext())
                        binding.templatesContainerJournal.addView(sliderView)
                        sliderView.setTemplate(it)
                        sliderView.setCallback(this)
                    }
                }
            }
        }

        binding.submitButtonJournal.setOnClickListener {
            journalViewModel.insertEntry(
                Entry(
                    timestamp = System.currentTimeMillis(),
                    mood = binding.moodBarJournal.value,
                    sleep = binding.sleepBarJournal.value,
                    newSpots = binding.spotsBarJournal.value,
                    rating = binding.rateBarJournal.value,
                    templateValues = this.templateValues
                )
            )
        }

        binding.customizeImageViewJournal.setOnClickListener {
            findNavController().navigate(JournalFragmentDirections.actionJournalFragmentToTemplatesFragment())
        }

    }

    override fun onValueChanged(templateTimestamp: Long, value: Int) {
        templateValues[templateTimestamp.toString()] = value
    }

}