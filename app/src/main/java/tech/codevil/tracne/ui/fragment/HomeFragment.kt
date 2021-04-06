package tech.codevil.tracne.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.databinding.FragmentHomeBinding
import tech.codevil.tracne.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

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

        mainViewModel.enableWritingToday.observe(viewLifecycleOwner, {
            binding.writeCardHome.isClickable = it
            if (it) binding.writeSpielHome.text =
                "You haven't written an entry today.\nClick here to log your progress"
            else binding.writeSpielHome.text = "All done! Have a great day"
//            binding.writeButtonHome.isEnabled = it
        })
        mainViewModel.entries.observe(viewLifecycleOwner, { entries ->
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