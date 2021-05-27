package tech.codevil.tracne.ui.journal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.R
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.common.util.Extensions.scaleDownToGone
import tech.codevil.tracne.common.util.Extensions.scaleUpToVisible
import tech.codevil.tracne.databinding.FragmentJournalBinding
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
    val args: JournalFragmentArgs by navArgs()

    private val templateValues: MutableMap<String, Int> = mutableMapOf()
    private val templateIds: MutableMap<String, Int> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

        journalViewModel.templatesAndInitialEntry.observe(viewLifecycleOwner) { (templates, initial) ->
            Log.d(javaClass.simpleName, "templates $templates")
            binding.templatesContainerJournal.removeAllViews()
            templates.map {
                when (it.type) {
                    Constants.TEMPLATE_TYPE_YES_NO -> {
                        val yesNoView = YesNoTemplateView(requireContext())
                        binding.templatesContainerJournal.addView(yesNoView)
                        yesNoView.setTemplate(it)
                        yesNoView.setCallback(this)
                        yesNoView.id = View.generateViewId()
                        templateIds[it.id()] = yesNoView.id
                        initial?.values?.get(it.id())?.let { value ->
                            yesNoView.setValue(value)
                            templateValues[it.id()] = value
                        }
                    }
                    Constants.TEMPLATE_TYPE_SLIDER -> {
                        val sliderView = SliderTemplateView(requireContext())
                        binding.templatesContainerJournal.addView(sliderView)
                        sliderView.setTemplate(it)
                        sliderView.setCallback(this)
                        sliderView.id = View.generateViewId()
                        templateIds[it.id()] = sliderView.id
                        initial?.values?.get(it.id())?.let { value ->
                            sliderView.setValue(value)
                            templateValues[it.id()] = value
                        }
                    }
                    else -> {
                    }
                }
            }
            val hasEntry = initial != null
            binding.templatesBlockerJournal.isClickable = hasEntry
            binding.submitButtonJournal.text =
                if (hasEntry) getString(R.string.update_entry) else getString(R.string.add_entry)
            if (hasEntry) {
                binding.fadingEdgeJournal.visibility = View.GONE
                binding.submitButtonJournal.scaleDownToGone()
                binding.editJournal.setOnClickListener {
                    binding.editJournal.scaleDownToGone()
                    binding.fadingEdgeJournal.visibility = View.VISIBLE
                    binding.submitButtonJournal.scaleUpToVisible()
                    binding.templatesBlockerJournal.isClickable = false
                }
                binding.editJournal.scaleUpToVisible()
            } else {
                binding.fadingEdgeJournal.visibility = View.VISIBLE
                binding.submitButtonJournal.scaleUpToVisible()
                binding.editJournal.scaleDownToGone()
                binding.editJournal.setOnClickListener(null)
            }
        }

        journalViewModel.showError.observe(viewLifecycleOwner) { errors ->
            Log.d(javaClass.simpleName, "errors.toString() = $errors")
            errors.forEach {
                val selectedView = getTemplateView(templateIds[it.key] ?: -1)
                Log.d(javaClass.simpleName, "errors selectedViewId = ${templateIds[it.key] ?: -1}")
                Log.d(javaClass.simpleName, "errors selectedView = $selectedView")
                (selectedView as? YesNoTemplateView)?.showError(it.value)
                (selectedView as? SliderTemplateView)?.showError(it.value)
            }
        }

        binding.submitButtonJournal.setOnClickListener {
            journalViewModel.insertEntry()
        }
        binding.backButtonJournal.setOnClickListener { findNavController().navigateUp() }
        journalViewModel.timestamp.value = args.timestamp
    }

    override fun onValueChanged(templateTimestamp: Long, value: Int) {
        templateValues[templateTimestamp.toString()] = value
        journalViewModel.values.value = templateValues

        val selectedView = getTemplateView(templateIds[templateTimestamp.toString()] ?: -1)
        (selectedView as? YesNoTemplateView)?.hideError()
        (selectedView as? SliderTemplateView)?.hideError()
    }

    fun getTemplateView(viewId: Int): View? {
        for (view in binding.templatesContainerJournal.children) Log.d(javaClass.simpleName,
            "id ${view.id}")
        return binding.templatesContainerJournal.findViewById(viewId)
    }

}