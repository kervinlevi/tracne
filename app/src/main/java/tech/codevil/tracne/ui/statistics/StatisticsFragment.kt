package tech.codevil.tracne.ui.statistics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.gson.Gson
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
    private val chipId = mutableMapOf<String, Int>()

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

        statisticsViewModel.graphsFromEntries.observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, Gson().toJson(it).toString())
            binding.graphView.setGraphs(it)
        }

        statisticsViewModel.parameters.observe(viewLifecycleOwner) { parameters ->
            parameters.forEach {
                if (chipId.containsKey(it.id)) {
                    val chip = binding.graphTogglesChipGroup.findViewById(chipId[it.id]!!) as Chip
                    chip.text = it.label
                    chip.isChecked = it.isChecked
                    chip.isEnabled = it.isEnabled
                } else {
                    val chip = Chip(requireContext())
                    chip.id = View.generateViewId()
                    chip.isCheckable = true
                    chip.isCheckedIconVisible = true
                    chip.text = it.label
                    chip.isChecked = it.isChecked
                    chip.isEnabled = it.isEnabled
                    chip.setOnCheckedChangeListener { _, _ ->
                        statisticsViewModel.onToggleParameter(it.id)
                    }
                    chipId[it.id] = chip.id
                    binding.graphTogglesChipGroup.addView(chip)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}