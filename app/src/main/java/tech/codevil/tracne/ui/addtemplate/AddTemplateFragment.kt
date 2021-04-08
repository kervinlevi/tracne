package tech.codevil.tracne.ui.addtemplate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.R
import tech.codevil.tracne.common.util.Constants.QUESTION_TYPE_NUMERIC
import tech.codevil.tracne.common.util.Constants.QUESTION_TYPE_SLIDER
import tech.codevil.tracne.common.util.Constants.QUESTION_TYPE_YES_NO
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.databinding.FragmentAddTemplateBinding

/**
 * Created by kervin.decena on 31/03/2021.
 */
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
            addTemplateViewModel.addTemplate(
                binding.labelInputEditTextAddTemplate.text.toString(),
                binding.inputEditTextAddTemplate.text.toString(),
                typeRadioValue.getOrElse(
                    binding.typeRadioGroupAddTemplate.checkedRadioButtonId,
                    { "" }),
                binding.minInputEditTextAddTemplate.text.toString(),
                binding.maxInputEditTextAddTemplate.text.toString()
            )
        }
        binding.typeRadioGroupAddTemplate.setOnCheckedChangeListener { _, checkedId ->
            addTemplateViewModel.onTypeSelected(typeRadioValue.getOrElse(checkedId, { "" }))
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
                    findNavController().navigate(AddTemplateFragmentDirections.actionAddTemplateFragmentToTemplatesFragment())
                }
                is DataState.Loading -> {
                    binding.submitButtonAddTemplate.isEnabled = false
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}