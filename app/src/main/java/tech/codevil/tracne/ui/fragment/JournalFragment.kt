package tech.codevil.tracne.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_journal.*
import tech.codevil.tracne.databinding.FragmentJournalBinding
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.ui.viewmodel.MainViewModel

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding: FragmentJournalBinding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

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
        mainViewModel.date.observe(viewLifecycleOwner, {
            date_journal.text = it
        })

        submit_button_journal.setOnClickListener {
            mainViewModel.insertEntry(
                Entry(
                    timestamp = System.currentTimeMillis(),
                    mood = mood_bar_journal.progress,
                    sleep = sleep_bar_journal.progress,
                    newSpots = spots_bar_journal.progress,
                    rating = rate_bar_journal.progress
                )
            )
        }
    }

}