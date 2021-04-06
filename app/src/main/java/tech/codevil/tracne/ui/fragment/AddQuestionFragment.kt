package tech.codevil.tracne.ui.fragment

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
import tech.codevil.tracne.databinding.FragmentAddQuestionBinding
import tech.codevil.tracne.model.Question
import tech.codevil.tracne.ui.viewmodel.AddQuestionViewModel

/**
 * Created by kervin.decena on 31/03/2021.
 */
@AndroidEntryPoint
class AddQuestionFragment : Fragment() {

    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!

    private val typeRadioValue = mapOf(
        R.id.yes_no_radio_add_question to QUESTION_TYPE_YES_NO,
        R.id.numeric_radio_add_question to QUESTION_TYPE_NUMERIC,
        R.id.slider_radio_add_question to QUESTION_TYPE_SLIDER
    )

    private val addQuestionViewModel: AddQuestionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddQuestionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButtonAddQuestion.setOnClickListener {
            addQuestionViewModel.addQuestion(
                binding.labelInputEditTextAddQuestion.text.toString(),
                binding.inputEditTextAddQuestion.text.toString(),
                typeRadioValue.getOrElse(
                    binding.typeRadioGroupAddQuestion.checkedRadioButtonId,
                    { "" }),
                binding.minInputEditTextAddQuestion.text.toString(),
                binding.maxInputEditTextAddQuestion.text.toString()
            )
        }
        binding.typeRadioGroupAddQuestion.setOnCheckedChangeListener { group, checkedId ->
            addQuestionViewModel.onTypeSelected(typeRadioValue.getOrElse(checkedId, { "" }))
        }
        addQuestionViewModel.minMaxVisible.observe(viewLifecycleOwner, { visible ->
            val visibility = if (visible) View.VISIBLE else View.GONE
            binding.minMaxGroupAddQuestion.visibility = visibility
        })
        addQuestionViewModel.addSuccess.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Error -> {
                    binding.submitButtonAddQuestion.isEnabled = true
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataState.Success -> {
                    Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(AddQuestionFragmentDirections.actionAddQuestionFragmentToCustomizeFragment())
                }
                is DataState.Loading -> {
                    binding.submitButtonAddQuestion.isEnabled = false
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}