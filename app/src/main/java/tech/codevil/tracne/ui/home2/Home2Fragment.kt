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
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.databinding.FragmentHome2Binding
import tech.codevil.tracne.ui.home2.components.Home2Adapter
import tech.codevil.tracne.ui.home2.components.HomeCalendar
import tech.codevil.tracne.ui.home2.components.TemplateGraph
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DAY_OF_WEEK

/**
 * Created by kervin.decena on 21/03/2021.
 */
@AndroidEntryPoint
class Home2Fragment : Fragment(), Home2Adapter.Listener {

    private var _binding: FragmentHome2Binding? = null
    private val binding get() = _binding!!

    private val home2ViewModel: Home2ViewModel by viewModels()
    private val home2Adapter = Home2Adapter(this)

    private val weeklyPair: Pair<Long, Long> by lazy {
        val calendar = Calendar.getInstance()
        calendar.set(DAY_OF_WEEK, 1)
        calendar.setMinTime()
        val start = calendar.timeInMillis
        calendar.set(DAY_OF_WEEK, calendar.getActualMaximum(DAY_OF_WEEK))
        calendar.setMaxTime()
        val end = calendar.timeInMillis
        Pair(start, end)
    }

    private val monthlyPair: Pair<Long, Long> by lazy {
        val calendar = Calendar.getInstance()
        calendar.set(DAY_OF_MONTH, 1)
        calendar.setMinTime()
        val start = calendar.timeInMillis
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH))
        calendar.setMaxTime()
        val end = calendar.timeInMillis
        Pair(start, end)
    }

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

        home2ViewModel.entries.observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, "entries $it")
        }

        home2ViewModel.parameters.observe(viewLifecycleOwner) {
            Log.d(javaClass.simpleName, "parameters $it")
            home2Adapter.setParameterItems(it)
        }
        home2ViewModel.weeklyCalendar.observe(viewLifecycleOwner) { home2Adapter.calendarList = it }
        home2Adapter.isWeekly = home2ViewModel.isWeekly.value != false
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
        TODO("Not yet implemented")
    }

    override fun onWeeklyPicked() {
        home2ViewModel.duration.value = weeklyPair
        home2ViewModel.isWeekly.value = true
    }

    override fun onMonthlyPicked() {
        home2ViewModel.duration.value = monthlyPair
        home2ViewModel.isWeekly.value = false
    }

    override fun onParameterClicked(parameter: TemplateGraph?) {
        Log.d(javaClass.simpleName, "param = $parameter")
        parameter?.let {
            findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToParameterFragment(
                it))
        }
    }

    override fun onWriteClicked() {
//        findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToJournalFragment2())
    }

    override fun onClickCalendar(calendar: HomeCalendar) {
        findNavController().navigate(Home2FragmentDirections.actionHome2FragmentToJournalFragment2(
            calendar.timestamp))
    }
}