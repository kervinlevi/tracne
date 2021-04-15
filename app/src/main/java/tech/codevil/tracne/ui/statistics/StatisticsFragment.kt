package tech.codevil.tracne.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.databinding.FragmentStatisticsBinding
import java.util.*

/**
 * Created by kervin.decena on 10/04/2021.
 */
@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statisticsViewModel.entriesWithinTimestamp.observe(viewLifecycleOwner) { entries ->
            val stringBuilder = StringBuilder()
            entries.map {
                stringBuilder.append("${Constants.DAY_FORMAT.format(Date(it.timestamp))}\tSleep = ${it.sleep}\tSpots = ${it.newSpots}\n")
            }
            binding.entriesStatistics.text = stringBuilder.toString()
        }

        statisticsViewModel.graphsFromEntries.observe(
            viewLifecycleOwner,
            binding.graphView::setGraphs
        )
    }


}