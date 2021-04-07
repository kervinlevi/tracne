package tech.codevil.tracne.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.R
import tech.codevil.tracne.databinding.FragmentHomeBinding
import tech.codevil.tracne.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.enableWritingToday.observe(viewLifecycleOwner, {
            binding.writeCardHome.isClickable = it
            binding.writeSpielHome.text =
                if (it) getString(R.string.not_written_an_entry) else getString(R.string.all_done)
        })
        homeViewModel.entries.observe(viewLifecycleOwner, { entries ->
            val stringBuilder = StringBuilder()
            entries.map {
                stringBuilder.append("${DATE_FORMATTER.format(Date(it.timestamp))} Skin rating = ${it.rating}\n")
            }
            binding.entriesTextHome.text = stringBuilder.toString()
        })
        binding.writeCardHome.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToJournalFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val DATE_FORMATTER = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
    }
}