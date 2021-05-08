package tech.codevil.tracne.ui.home2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.common.util.Constants.RANGE_FORMAT
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.databinding.FragmentHome2Binding
import tech.codevil.tracne.ui.home2.components.Home2Adapter
import tech.codevil.tracne.ui.home2.components.HomeCalendar
import tech.codevil.tracne.ui.home2.components.HomeCalendarAdapter
import tech.codevil.tracne.ui.home2.components.TemplateGraph
import java.util.*

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class Home2Fragment : Fragment(), Home2Adapter.Listener {

    private var _binding: FragmentHome2Binding? = null
    private val binding get() = _binding!!

    private val home2ViewModel: Home2ViewModel by viewModels()
    private val home2Adapter = Home2Adapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHome2Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.home2RecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = home2Adapter
        }

        home2ViewModel.enableWritingToday.observe(viewLifecycleOwner) {
            home2Adapter.writingTodayEnabled = it
        }

        home2ViewModel.entries.observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, "entries $it")
        }

        home2ViewModel.parameters.observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, "parameters $it")
            home2Adapter.setParameterItems(it)
        }
        home2ViewModel.duration.observe(viewLifecycleOwner) {
            val date =
                RANGE_FORMAT.format(Date(it.first)) + " — " + RANGE_FORMAT.format(Date(it.second))
            home2Adapter.date = date
        }
        home2ViewModel.weeklyCalendar.observe(viewLifecycleOwner) { home2Adapter.calendarList = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCalendarOptionClicked() {
        TODO("Not yet implemented")
    }

    override fun onUserOptionClicked() {
        TODO("Not yet implemented")
    }

    override fun onTemplateOptionClicked() {
        findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToTemplatesFragment2())
    }

    override fun onAboutOptionClicked() {
        TODO("Not yet implemented")
    }

    override fun onDatePickerClicked() {
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
            home2ViewModel.duration.value = Pair(start, end)
        }
        picker.show(requireActivity().supportFragmentManager, "date_range_picker")
    }

    override fun onParameterClicked(parameter: TemplateGraph?) {
        Log.d(javaClass.simpleName, "param = $parameter")
        parameter?.let {
            findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToParameterFragment(
                it))
        }
    }

    override fun onWriteClicked() {
        findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToJournalFragment2())
    }

    override fun onClickCalendar(calendar: HomeCalendar) {
        if (calendar.isToday && !calendar.isChecked) {
            findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToJournalFragment2())
        }
        else {
            Toast.makeText(requireContext(), "$calendar", Toast.LENGTH_SHORT).show()
        }
    }
}