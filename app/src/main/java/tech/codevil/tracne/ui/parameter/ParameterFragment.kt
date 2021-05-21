package tech.codevil.tracne.ui.parameter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.R
import tech.codevil.tracne.common.util.Constants.RANGE_FORMAT
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.databinding.FragmentParameterBinding
import tech.codevil.tracne.databinding.ViewLayoutTemplateOptionHeaderBinding
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.ui.parameter.components.ParameterDataAdapter
import java.util.*

@AndroidEntryPoint
class ParameterFragment : Fragment() {

    private var _binding: FragmentParameterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ParameterViewModel by viewModels()
    val args: ParameterFragmentArgs by navArgs()

    private val templateOptionsAdapter by lazy {
        ArrayAdapter<String>(requireContext(), R.layout.view_layout_template_option)
    }
    private val templateOptions = mutableListOf<Template?>()

    private val adapter = ParameterDataAdapter()

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

        binding.parameter1CardFragmentParameter.apply {
            indicatorViewCardParam.setBackgroundResource(R.drawable.ic_circle_pastel_blue)
            selectionIconCardParam.visibility = View.GONE
            labelCardParam.text = param.template.label
            guidingQuestionCardParam.text = param.template.guidingQuestion
            root.isClickable = false
        }
        binding.parameter2CardFragmentParameter.root.isClickable = true
        binding.parameter2CardFragmentParameter.root.setOnClickListener { showTemplatePicker() }
        binding.datePickerParameter.setOnClickListener { showDateRangePicker() }
        binding.backButtonParameter.setOnClickListener { findNavController().navigateUp() }


        binding.dataRecyclerViewParameter.adapter = adapter
        binding.dataRecyclerViewParameter.layoutManager = LinearLayoutManager(requireContext())

        viewModel.entries.observe(viewLifecycleOwner, adapter::setEntries)
        viewModel.template1.observe(viewLifecycleOwner, adapter::setTemplate1)
        viewModel.template2.observe(viewLifecycleOwner, adapter::setTemplate2)

        viewModel.duration.observe(viewLifecycleOwner) {
            binding.datePickerParameter.text =
                getString(R.string.date_range,
                    RANGE_FORMAT.format(Date(it.first)),
                    RANGE_FORMAT.format(Date(it.second)))
        }
        viewModel.graphs.observe(viewLifecycleOwner) { graphs ->
            if (graphs.isNotEmpty()) {
                binding.statisticsGraphParameter.setGraph1(graphs[0])
            }
            if (graphs.size > 1) {
                binding.statisticsGraphParameter.setGraph2(graphs[1])
            } else {
                binding.statisticsGraphParameter.clearGraph2()
            }
        }
        viewModel.secondTemplateOptions.observe(viewLifecycleOwner) { templates ->
            templateOptions.clear()
            templateOptions.addAll(templates)
            templateOptionsAdapter.clear()
            templateOptionsAdapter.addAll(templateOptions.map {
                it?.label ?: getString(R.string.none)
            })
        }
        viewModel.duration.value = Pair(param.startTimestamp, param.endTimestamp)
        viewModel.template2.observe(viewLifecycleOwner) { template ->
            if (template == null) {
                binding.parameter2CardFragmentParameter.apply {
                    indicatorViewCardParam.setBackgroundResource(R.drawable.ic_circle_hollow)
                    labelCardParam.setTextColor(ResourcesCompat.getColor(resources,
                        R.color.cloud,
                        null))
                    guidingQuestionCardParam.setTextColor(ResourcesCompat.getColor(resources,
                        R.color.cloud,
                        null))
                    selectionIconCardParam.visibility = View.GONE
                    labelCardParam.text = getString(R.string.compare_to_ellipsis)
                    guidingQuestionCardParam.text = getString(R.string.tap_to_select)
                }
            } else {
                binding.parameter2CardFragmentParameter.apply {
                    indicatorViewCardParam.setBackgroundResource(R.drawable.ic_circle_pastel_red)
                    labelCardParam.setTextColor(ResourcesCompat.getColor(resources,
                        R.color.charcoal,
                        null))
                    guidingQuestionCardParam.setTextColor(ResourcesCompat.getColor(resources,
                        R.color.charcoal,
                        null))
                    selectionIconCardParam.visibility = View.VISIBLE
                    labelCardParam.text = template.label
                    guidingQuestionCardParam.text = template.guidingQuestion
                }
            }
        }
        viewModel.template1.value = param.template
        viewModel.template2.value = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val calendarConstraints = CalendarConstraints.Builder()
        val startOf2021 = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2021)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        val endOfCurrentMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.timeInMillis
        calendarConstraints.setStart(startOf2021).setEnd(endOfCurrentMonth)
        builder.setCalendarConstraints(calendarConstraints.build())
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
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

    private fun showTemplatePicker() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setCustomTitle(ViewLayoutTemplateOptionHeaderBinding.inflate(
            layoutInflater).root)
        alertDialogBuilder.setAdapter(templateOptionsAdapter) { _, which ->
            viewModel.template2.value = templateOptions.getOrNull(which)
        }
        alertDialogBuilder.create().show()
    }
}