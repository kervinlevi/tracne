package tech.codevil.tracne.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.databinding.FragmentCustomizeBinding
import tech.codevil.tracne.ui.recyclerviewcomponent.CustomizeAdapter
import tech.codevil.tracne.ui.recyclerviewcomponent.CustomizeItemDecoration

/**
 * Created by kervin.decena on 31/03/2021.
 */
@AndroidEntryPoint
class CustomizeFragment : Fragment(), CustomizeAdapter.Listener {

    private var _binding: FragmentCustomizeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomizeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CustomizeAdapter(this)
        binding.customizeRecyclerView.apply {
            addItemDecoration(CustomizeItemDecoration())
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickAddCustomQuestion() {
        findNavController().navigate(CustomizeFragmentDirections.actionCustomizeFragmentToAddQuestionFragment())
    }

}