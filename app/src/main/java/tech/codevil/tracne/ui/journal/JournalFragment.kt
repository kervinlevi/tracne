package tech.codevil.tracne.ui.journal

import android.os.Bundle
import android.util.Log
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
import java.util.*

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class JournalFragment : Fragment(), TemplateViewCallback {

    private var _binding: FragmentJournalBinding? = null
    private val binding: FragmentJournalBinding get() = _binding!!

    private val journalViewModel: JournalViewModel by viewModels()
    private val templateValues: MutableMap<String, Int> = mutableMapOf()
    private val templateIds: MutableMap<String, Int> = mutableMapOf()

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
//                    findNavController().navigate(JournalFragmentDirections.actionJournalFragmentToHomeFragment())
                    findNavController().navigateUp()
                }
                is DataState.Loading -> {
                    binding.submitButtonJournal.isEnabled = false
                }
            }
        }
        journalViewModel.templates.observe(viewLifecycleOwner) { it ->
            Log.d(javaClass.simpleName, "templates $it")
            binding.templatesContainerJournal.removeAllViews()
            it.map {
                when(it.type) {
                    Constants.TEMPLATE_TYPE_YES_NO -> {
                        val yesNoView = YesNoTemplateView(requireContext())
                        binding.templatesContainerJournal.addView(yesNoView)
                        yesNoView.setTemplate(it)
                        yesNoView.setCallback(this)
                        yesNoView.id = View.generateViewId()
                        templateIds[it.id()] = yesNoView.id
                        templateValues[it.id()]?.also(yesNoView::setValue)
                    }
                    Constants.TEMPLATE_TYPE_SLIDER -> {
                        val sliderView = SliderTemplateView(requireContext())
                        binding.templatesContainerJournal.addView(sliderView)
                        sliderView.setTemplate(it)
                        sliderView.setCallback(this)
                        sliderView.id = View.generateViewId()
                        templateIds[it.id()] = sliderView.id
                        templateValues[it.id()]?.also(sliderView::setValue)
                    }
                    else -> {}
                }
            }
        }

        journalViewModel.showError.observe(viewLifecycleOwner) { errors ->
            errors.forEach {
                val viewId = templateIds[it] ?: -1
                val selectedView : View? = binding.templatesContainerJournal.findViewById(viewId)
                (selectedView as? YesNoTemplateView)?.showError()
                (selectedView as? SliderTemplateView)?.showError() //todo: hide error after
            }
        }

        binding.submitButtonJournal.setOnClickListener {
            journalViewModel.insertEntry()
        }

        binding.customizeImageViewJournal.setOnClickListener {
            findNavController().navigate(JournalFragmentDirections.actionJournalFragmentToTemplatesFragment())
        }

    }

    override fun onValueChanged(templateTimestamp: Long, value: Int) {
        templateValues[templateTimestamp.toString()] = value
        journalViewModel.values.value = templateValues
    }

}