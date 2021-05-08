package tech.codevil.tracne.ui.home2

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants.DAY_FORMAT
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.repository.TemplateRepository
import tech.codevil.tracne.ui.home2.components.HomeCalendar
import tech.codevil.tracne.ui.home2.components.TemplateGraph
import tech.codevil.tracne.ui.statistics.MultipleGraphView.Graph
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
@HiltViewModel
class Home2ViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val templateRepository: TemplateRepository,
) : ViewModel() {

    val enableWritingToday: LiveData<Boolean>

    val duration = MutableLiveData<Pair<Long, Long>>()
    val entries = Transformations.switchMap(duration) {
        entryRepository.observeEntriesWithin(it.first, it.second)
    }
    val parameters: LiveData<List<TemplateGraph>>
    private val templates = templateRepository.observeTemplates()
    private val entriesAndTemplates = MediatorLiveData<Pair<List<Entry>?, List<Template>?>>()

    private val _weeklyCalendar = MutableLiveData<List<HomeCalendar>>()
    val weeklyCalendar: LiveData<List<HomeCalendar>> = _weeklyCalendar

    init {
        viewModelScope.launch {
            entryRepository.loadMockData()
        }
        val today = DAY_FORMAT.format(Calendar.getInstance().time)
        enableWritingToday = Transformations.map(entryRepository.getEntryByDateLiveData(today)) {
            it.isEmpty()
        }

        entriesAndTemplates.addSource(entries, this::combineEntriesAndTemplates)
        entriesAndTemplates.addSource(templates, this::combineEntriesAndTemplates)

        parameters = Transformations.map(entriesAndTemplates) {
            val entries = it.first ?: emptyList()
            val temps = it.second ?: emptyList()
            val parameterItems = mutableListOf<TemplateGraph>()

            val start = duration.value?.first ?: 0L
            val end = duration.value?.second ?: 0L

            if (start == 0L || end == 0L) {
                return@map emptyList()
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = start
            calendar.setMinTime()
            val noTimeStart = calendar.timeInMillis
            val xMin = 0

            calendar.timeInMillis = end
            calendar.setMinTime()
            val noTimeEnd = calendar.timeInMillis
            val xMax = daysBetween(noTimeStart, noTimeEnd)


            val paramValues = mutableMapOf<String, MutableMap<Int, Int>>()
            temps.forEachIndexed { index, template ->
                parameterItems.add(TemplateGraph(template,
                    Graph(xMin,
                        xMax,
                        template.min,
                        template.max,
                        mutableMapOf(),
                        Color.parseColor("#8DCAD4")),
                    noTimeStart, noTimeEnd
                ))
            }
            parameterItems.forEach { item ->
                paramValues[item.template.id()] = item.graph.valuesMap
            }

            entries.forEachIndexed { i, entry ->
                val date = entry.day
                val x = daysBetween(noTimeStart, date.time)
                entry.values.map { pair -> paramValues[pair.key]?.put(x, pair.value) }
            }
            parameterItems
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, 1)
        calendar.setMinTime()

        val weekStart = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_WEEK, 7)
        calendar.setMaxTime()
        val weekEnd = calendar.timeInMillis

        duration.value = Pair(weekStart, weekEnd)



        loadMockDataIfEmpty()
        computeCalendar()
    }

    private fun combineEntriesAndTemplates(any: Any) {
        entriesAndTemplates.value = Pair(entries.value, templates.value)
    }

    private fun daysBetween(start: Long, end: Long): Int {
        return ((end - start) / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun computeCalendar() = viewModelScope.launch { //TODO: observe entries instead
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, 1)
        calendar.setMinTime()

        val weekStart = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_WEEK, 7)
        calendar.setMaxTime()
        val weekEnd = calendar.timeInMillis

        entryRepository.getEntries(weekStart, weekEnd).collect { entries ->
            val mutableListCalendar = mutableListOf<HomeCalendar>()
            val today = DAY_FORMAT.format(Calendar.getInstance().time)
            var isFutureDate = false
            for (day in 1..7) {
                calendar.set(Calendar.DAY_OF_WEEK, day)
                val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.ALL_STYLES,
                    Locale.ENGLISH)
                    ?.substring(0, 3) ?: ""
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val formattedDate = DAY_FORMAT.format(calendar.time)
                val isToday = today == formattedDate
                val isChecked =
                    entries.firstOrNull { entry -> formattedDate == DAY_FORMAT.format(entry.day) } != null
                mutableListCalendar.add(HomeCalendar(isChecked,
                    dayOfWeek,
                    dayOfMonth,
                    isToday,
                    isFutureDate))
                if (isToday) isFutureDate = true
            }
            _weeklyCalendar.value = mutableListCalendar
        }
    }

    private fun loadMockDataIfEmpty() = viewModelScope.launch {
        Log.d(javaClass.simpleName, "viewModelScope.launch")
        entryRepository.getEntries().collect {
            if (it.isNotEmpty()) {
                return@collect
            }
            Log.d(javaClass.simpleName, "entryRepository.getEntries().collect")
            templateRepository.getTemplates().collect { templates ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, Calendar.APRIL)
                val days =
                    cal.getActualMinimum(DAY_OF_MONTH)..cal.getActualMaximum(
                        DAY_OF_MONTH)
                for (day in days) {
                    cal.set(DAY_OF_MONTH, day)
                    val valuesMap = mutableMapOf<String, Int>()
                    templates.forEach { template ->
                        valuesMap[template.timestamp.toString()] =
                            (template.min..template.max).random()
                    }
                    val entry = Entry(cal.timeInMillis,
                        cal.time,
                        valuesMap,
                        cal.timeInMillis)
                    entryRepository.insertEntry(entry)
                }
            }
        }
    }
}