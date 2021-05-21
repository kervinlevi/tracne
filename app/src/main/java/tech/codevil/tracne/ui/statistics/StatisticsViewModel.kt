package tech.codevil.tracne.ui.statistics

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.repository.TemplateRepository
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    entryRepository: EntryRepository,
    templateRepository: TemplateRepository
) : ViewModel() {

    val timestampStart = MutableLiveData<Long>()
    val timestampEnd = MutableLiveData<Long>()
    val timestampLiveData = MediatorLiveData<Pair<Long, Long>>()
    val entriesWithinTimestamp: LiveData<List<Entry>>
    val graphsFromEntries: LiveData<List<Graph>>
    val parameters = MutableLiveData<List<Parameter>>()
    val templates: LiveData<List<Template>>

    init {
        Log.d(javaClass.simpleName, "Statistics")
        templates = Transformations.map(templateRepository.observeTemplates()) {
            Log.d(javaClass.simpleName, "Transformations.map(templateRepository.observeTemplates()")
            val map = it.map { template -> template.id() to template }.toMap().toMutableMap()
            parameters.value?.forEach { parameter -> map.remove(parameter.template.id()) }
            val newParams = parameters.value?.toMutableList() ?: mutableListOf()
            map.forEach { (_, value) ->
                newParams.add(Parameter(value))
            }
            parameters.value = newParams
            Log.d(javaClass.simpleName, "newParams = $newParams")
            it
        }

        timestampLiveData.addSource(timestampStart, this::combineTimestamp)
        timestampLiveData.addSource(timestampEnd, this::combineTimestamp)
        timestampLiveData.addSource(parameters, this::combineTimestamp)
        timestampLiveData.addSource(templates, this::combineTimestamp)

        entriesWithinTimestamp = Transformations.switchMap(timestampLiveData) {
            entryRepository.observeEntriesWithin(it.first, it.second)
        }
        graphsFromEntries = Transformations.map(entriesWithinTimestamp) { entries ->
            val templateList = templates.value ?: listOf()
            val templateMap =
                templateList.map { template -> template.timestamp.toString() to template }.toMap()
            val selectedParameters = mutableListOf<String>()
            parameters.value?.forEach { if (it.isChecked) selectedParameters.add(it.template.id()) }
            Log.d("graphsFromEntries", selectedParameters.toString())

            val graph = mutableListOf<Graph>()
            val listValuesMap = mutableListOf<MutableMap<Int, Int>>().apply {
                selectedParameters.map { add(mutableMapOf()) }
            }
            val start = timestampStart.value
            val end = timestampEnd.value
            if (start == null || end == null || start > end) {
                return@map graph
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

            entries.map {
                val date = it.day
                val x = daysBetween(noTimeStart, date.time)
                selectedParameters.forEachIndexed { index, param ->
                    if (it.values.containsKey(param)) {
                        listValuesMap[index][x] = it.values[param]!!

                    }
                }

            }
            selectedParameters.forEachIndexed { index, param ->
                if (templateMap.containsKey(param)) {
                    graph.add(
                        Graph(
                            xMin,
                            xMax,
                            templateMap[param]?.min ?: 0,
                            templateMap[param]?.max ?: 10,
                            listValuesMap[index],
                            Color.BLACK
                        )
                    )
                }
            }
            graph
        }

        parameters.value = listOf()


        val currentMonth = Calendar.getInstance().apply { //start of month
            set(DAY_OF_MONTH, getActualMinimum(DAY_OF_MONTH))
            setMinTime()
        }
        timestampStart.value = currentMonth.timeInMillis

        currentMonth.apply { //end of month
            set(DAY_OF_MONTH, getActualMaximum(DAY_OF_MONTH))
            setMaxTime()
        }
        timestampEnd.value = currentMonth.timeInMillis
    }

    private fun combineTimestamp(any: Any) {
        if (timestampStart.value != null && timestampEnd.value != null) {
            timestampLiveData.value = Pair(timestampStart.value!!, timestampEnd.value!!)
        }
    }

    private fun daysBetween(start: Long, end: Long): Int {
        return ((end - start) / (24 * 60 * 60 * 1000)).toInt()
    }

    fun onToggleParameter(id: String) {
        parameters.value?.find { it.template.id() == id }?.toggle()
        parameters.value = parameters.value
    }
}