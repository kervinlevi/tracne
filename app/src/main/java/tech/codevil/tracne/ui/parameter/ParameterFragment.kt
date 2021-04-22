package tech.codevil.tracne.ui.parameter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.common.util.Constants.RANGE_FORMAT
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.databinding.FragmentParameterBinding
import java.util.*

@AndroidEntryPoint
class ParameterFragment : Fragment() {

    private var _binding: FragmentParameterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ParameterViewModel by viewModels()
    val args: ParameterFragmentArgs by navArgs()

    private val chipId = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentParameterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val param = args.param

        viewModel.duration.observe(viewLifecycleOwner) {
            binding.datePickerParameter.text =
                RANGE_FORMAT.format(Date(it.first)) + " — " + RANGE_FORMAT.format(Date(it.second))
        }
        viewModel.graphs.observe(viewLifecycleOwner, binding.statisticsGraphParameter::setGraphs)
        viewModel.parameters.observe(viewLifecycleOwner) { parameters ->
            parameters.forEach {
                if (chipId.containsKey(it.id)) {
                    val chip = binding.chipGroupParameter.findViewById(chipId[it.id]!!) as Chip
                    chip.text = it.label
                } else {
                    val chip = Chip(requireContext())
                    chip.id = View.generateViewId()
                    chip.isCheckable = true
                    chip.isCheckedIconVisible = true
                    chip.text = it.label
                    chip.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            viewModel.selectedParameters.value = listOf(param.id, it.id)
                        }
                        else {
                            viewModel.selectedParameters.value = listOf(param.id)
                        }
                    }
                    chipId[it.id] = chip.id
                    binding.chipGroupParameter.addView(chip)
                }
            }
        }

//        binding.chipGroupParameter.setOnCheckedChangeListener { group, checkedId ->
//            chipId.forEach {
//                if (it.value.equals(checkedId)) {
//                    viewModel.selectedParameters.value = listOf(param.id, it.key)
//                    Log.d(javaClass.simpleName, viewModel.selectedParameters.value.toString())
//                }
//            }
//        }

        binding.labelParameter.text = param.label
        binding.datePickerParameter.setOnClickListener { showDateRangePicker() }
        viewModel.selectedParameters.value = listOf(param.id)
        viewModel.duration.value = Pair(param.startTimestamp, param.endTimestamp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.first ?: 0L
            calendar.setMinTime()
            val start = calendar.timeInMillis
            calendar.timeInMillis = it.second ?: 0L
            calendar.setMaxTime()
            val end = calendar.timeInMillis
            viewModel.duration.value = Pair(start, end)
        }
        picker.show(requireActivity().supportFragmentManager, "date_range_picker")
    }
}