package tech.codevil.tracne.ui.addtemplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import tech.codevil.tracne.R
import tech.codevil.tracne.common.util.Constants.QUESTION_TYPE_NUMERIC
import tech.codevil.tracne.common.util.Constants.QUESTION_TYPE_SLIDER
import tech.codevil.tracne.common.util.Constants.QUESTION_TYPE_YES_NO
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.databinding.FragmentAddTemplateBinding
import tech.codevil.tracne.common.util.Extensions.textWatcherFlow

/**
 * Created by kervin.decena on 31/03/2021.
 */
@FlowPreview
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddTemplateFragment : Fragment() {

    private var _binding: FragmentAddTemplateBinding? = null
    private val binding get() = _binding!!

    private val typeRadioValue = mapOf(
        R.id.yes_no_radio_add_template to QUESTION_TYPE_YES_NO,
        R.id.numeric_radio_add_template to QUESTION_TYPE_NUMERIC,
        R.id.slider_radio_add_template to QUESTION_TYPE_SLIDER
    )

    private val addTemplateViewModel: AddTemplateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTemplateBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButtonAddTemplate.setOnClickListener {
            addTemplateViewModel.onAddTemplateClicked()
        }
        binding.typeRadioGroupAddTemplate.setOnCheckedChangeListener { _, checkedId ->
            addTemplateViewModel.typeInput.value = typeRadioValue.getOrElse(checkedId, { "" })
        }

        lifecycleScope.launchWhenStarted {
            binding.labelInputEditTextAddTemplate.textWatcherFlow()
                .collect { input -> addTemplateViewModel.labelInput.value = input.toString() }
        }
        lifecycleScope.launchWhenStarted {
            binding.inputEditTextAddTemplate.textWatcherFlow()
                .collect { input ->
                    addTemplateViewModel.guidingQuestionInput.value = input.toString()
                }
        }
        lifecycleScope.launchWhenStarted {
            binding.minInputEditTextAddTemplate.textWatcherFlow()
                .collect { input ->
                    addTemplateViewModel.minInput.value = input.toString()
                }
        }
        lifecycleScope.launchWhenStarted {
            binding.maxInputEditTextAddTemplate.textWatcherFlow()
                .collect { input ->
                    addTemplateViewModel.maxInput.value = input.toString()
                }
        }

        addTemplateViewModel.minMaxVisible.observe(viewLifecycleOwner, { visible ->
            val visibility = if (visible) View.VISIBLE else View.GONE
            binding.minMaxGroupAddTemplate.visibility = visibility
        })
        addTemplateViewModel.addSuccess.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Error -> {
                    binding.submitButtonAddTemplate.isEnabled = true
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataState.Success -> {
                    Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddTemplateFragmentDirections.actionAddTemplateFragment2ToTemplatesFragment2())
                }
                is DataState.Loading -> {
                    binding.submitButtonAddTemplate.isEnabled = false
                }
            }
        })
        addTemplateViewModel.labelState.observe(viewLifecycleOwner, {
            binding.labelInputLayoutAddTemplate.isErrorEnabled = it.hasError
            binding.labelInputEditTextAddTemplate.error = it.error
        })
        addTemplateViewModel.guidingQuestionState.observe(viewLifecycleOwner, {
            binding.inputLayoutAddTemplate.isErrorEnabled = it.hasError
            binding.inputEditTextAddTemplate.error = it.error
        })
        addTemplateViewModel.typeState.observe(viewLifecycleOwner, {
            binding.typeErrorAddTemplate.text = it.error
            binding.typeErrorAddTemplate.visibility = if (it.hasError) View.VISIBLE else View.GONE
        })
        addTemplateViewModel.minState.observe(viewLifecycleOwner, {
            binding.minInputLayoutAddTemplate.isErrorEnabled = it.hasError
            binding.minInputEditTextAddTemplate.error = it.error
        })
        addTemplateViewModel.maxState.observe(viewLifecycleOwner, {
            binding.maxInputLayoutAddTemplate.isErrorEnabled = it.hasError
            binding.maxInputEditTextAddTemplate.error = it.error
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}