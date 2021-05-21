package tech.codevil.tracne.ui.parameter

import android.graphics.Color
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants.SHORTENED_DAY_FORMAT
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.repository.TemplateRepository
import tech.codevil.tracne.ui.home2.components.TemplateGraph
import tech.codevil.tracne.ui.statistics.Graph
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParameterViewModel @Inject constructor(
    entryRepository: EntryRepository,
    templateRepository: TemplateRepository,
) : ViewModel() {

    val template1 = MutableLiveData<Template>()
    val template2 = MutableLiveData<Template?>()

    private val _secondTemplateOptions = MediatorLiveData<List<Template?>>()
    val secondTemplateOptions: LiveData<List<Template?>> = _secondTemplateOptions

    private val graph1 = MediatorLiveData<TemplateGraph>()
    private val graph2 = MediatorLiveData<TemplateGraph?>()

    private val _graphs = MediatorLiveData<List<Graph>>()
    val graphs: LiveData<List<Graph>> = _graphs

    val duration = MutableLiveData<Pair<Long, Long>>()

    val entries: LiveData<List<Entry>> = Transformations.switchMap(duration) {
        entryRepository.observeEntriesWithin(it.first, it.second)
    }
    private val templates = templateRepository.observeTemplates()


    init {
        _secondTemplateOptions.addSource(template1, this::combineForSecondParameterOptions)
        _secondTemplateOptions.addSource(templates, this::combineForSecondParameterOptions)

        graph1.addSource(entries, this::combineForGraph1)
        graph1.addSource(template1, this::combineForGraph1)

        graph2.addSource(entries, this::combineForGraph2)
        graph2.addSource(template2, this::combineForGraph2)

        _graphs.addSource(graph1, this::combineForGraphs)
        _graphs.addSource(graph2, this::combineForGraphs)
    }

    private fun combineForSecondParameterOptions(any: Any) = viewModelScope.launch {
        val template1Value = template1.value
        val templateList = templates.value
        if (template1Value != null && templateList != null) {
            val secondParameterOptionsList: MutableList<Template?> = templateList.toMutableList()
            secondParameterOptionsList.remove(template1Value)
            secondParameterOptionsList.add(0, null)
            _secondTemplateOptions.value = secondParameterOptionsList
        }
    }

    private fun combineForGraph1(any: Any) = viewModelScope.launch {
        val template = template1.value
        val entryList = entries.value

        if (template != null && entryList != null) {
            graph1.value = getTemplateGraph(template, entryList, "#8DCAD4")
        }
    }

    private fun combineForGraph2(any: Any?) = viewModelScope.launch {
        val template = template2.value
        val entryList = entries.value
        if (template != null && entryList != null) {
            graph2.value = getTemplateGraph(template, entryList, "#FCB4B6")
        } else if (template == null) {
            graph2.value = null
        }
    }

    private fun getTemplateGraph(
        template: Template,
        entryList: List<Entry>,
        color: String,
    ): TemplateGraph {
        val start = duration.value?.first ?: 0L
        val end = duration.value?.second ?: 0L
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = start
        calendar.setMinTime()
        val noTimeStart = calendar.timeInMillis
        val xMin = 0

        calendar.timeInMillis = end
        calendar.setMaxTime()
        val noTimeEnd = calendar.timeInMillis
        val xMax = daysBetween(noTimeStart, noTimeEnd)

        val now = Calendar.getInstance().apply { setMinTime() }.timeInMillis
        val bigMarkerX =
            if (now < noTimeStart || now > noTimeEnd) -1 else daysBetween(noTimeStart, now)

        val graph = Graph(xMin,
            xMax,
            template.min,
            template.max,
            mutableMapOf(),
            Color.parseColor(color),
            xLabels = generateXLabels(noTimeStart, noTimeEnd),
            yLabels = generateYLabels(template),
            showBigMarkerAtX = bigMarkerX)

        entryList.forEach { entry ->
            val date = entry.day
            val x = daysBetween(noTimeStart, date.time)
            val valueAtX = entry.values[template.id()]
            if (valueAtX != null) {
                graph.valuesMap.put(x, valueAtX)
            }
        }
        return TemplateGraph(template, graph, noTimeStart, noTimeEnd)
    }

    private fun combineForGraphs(any: Any?) = viewModelScope.launch {
        val graphList = mutableListOf<Graph>()
        val templateGraphs1 = graph1.value
        val templateGraphs2 = graph2.value
        if (templateGraphs1 != null) {
            graphList.add(templateGraphs1.graph)
        }
        if (templateGraphs2 != null) {
            graphList.add(templateGraphs2.graph)
        }
        _graphs.value = graphList
    }

    private fun daysBetween(start: Long, end: Long): Int {
        return ((end - start) / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun generateXLabels(noTimeStart: Long, noTimeEnd: Long): Map<Int, String> {
        val daysBetween = daysBetween(noTimeStart, noTimeEnd)
        val map = mutableMapOf<Int, String>()
        val calendar = Calendar.getInstance().apply { timeInMillis = noTimeStart }

        when (daysBetween) {
            in 0..14 -> {
                for (i in 0..daysBetween) {
                    map[i] = ""
                }
                while (calendar.timeInMillis <= noTimeEnd) {
                    val x = daysBetween(noTimeStart, calendar.timeInMillis)
                    map[x] = SHORTENED_DAY_FORMAT.format(calendar.timeInMillis)
                    calendar.add(Calendar.DAY_OF_YEAR, 3)
                }
            }
            in 15..45 -> {
                while (calendar.timeInMillis <= noTimeEnd) {
                    val x = daysBetween(noTimeStart, calendar.timeInMillis)
                    map[x] = SHORTENED_DAY_FORMAT.format(calendar.timeInMillis)
                    calendar.add(Calendar.DAY_OF_YEAR, 7)
                }
            }
            else -> {
                val interval = daysBetween / 4
                while (calendar.timeInMillis <= noTimeEnd) {
                    val x = daysBetween(noTimeStart, calendar.timeInMillis)
                    map[x] = SHORTENED_DAY_FORMAT.format(calendar.timeInMillis)
                    calendar.add(Calendar.DAY_OF_YEAR, interval)
                }
            }
        }
        return map
    }

    private fun generateYLabels(template: Template): Map<Int, String> {
        val range = template.min..template.max
        val map = mutableMapOf<Int, String>()

        when (range.count()) {
            in 0..5 -> {
                range.forEach { map[it] = template.getLabelOf(it) }
            }
            in 6..10 -> {
                for (y in template.min..template.max step 2) {
                    map[y] = template.getLabelOf(y)
                }
            }
            else -> {
                val by4 = (range.last - range.first) / 4
                for (y in template.min..template.max step by4) {
                    map[y] = template.getLabelOf(y)
                }
            }
        }
        return map
    }

}