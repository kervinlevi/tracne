package tech.codevil.tracne.ui.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.databinding.FragmentTemplatesBinding
import tech.codevil.tracne.ui.templates.components.CustomizeAdapter
import tech.codevil.tracne.ui.templates.components.CustomizeItemDecoration

/**
 * Created by kervin.decena on 31/03/2021.
 */
@AndroidEntryPoint
class TemplatesFragment : Fragment(), CustomizeAdapter.Listener {

    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!

    private val templatesViewModel: TemplatesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CustomizeAdapter(this)
        binding.templatesRecyclerView.apply {
            addItemDecoration(CustomizeItemDecoration())
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            this.adapter = adapter
        }
        templatesViewModel.questions.observe(viewLifecycleOwner, {
            adapter.setItems(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickAddCustomQuestion() {
        findNavController().navigate(TemplatesFragmentDirections.actionTemplatesFragmentToAddTemplateFragment())
    }

}