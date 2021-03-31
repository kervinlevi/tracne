package tech.codevil.tracne.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.databinding.FragmentAddQuestionBinding

/**
 * Created by kervin.decena on 31/03/2021.
 */
@AndroidEntryPoint
class AddQuestionFragment : Fragment() {

    private var _binding: FragmentAddQuestionBinding? = null
    private val binding get() = _binding!!

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
            findNavController().navigate(AddQuestionFragmentDirections.actionAddQuestionFragmentToCustomizeFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}