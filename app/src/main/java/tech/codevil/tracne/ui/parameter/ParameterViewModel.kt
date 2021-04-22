package tech.codevil.tracne.ui.parameter

import android.graphics.Color
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.repository.TemplateRepository
import tech.codevil.tracne.ui.home2.components.ParameterItem
import tech.codevil.tracne.ui.statistics.MultipleGraphView
import tech.codevil.tracne.ui.statistics.MultipleGraphView.Graph
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParameterViewModel @Inject constructor(
    entryRepository: EntryRepository,
    templateRepository: TemplateRepository,
) : ViewModel() {


    val duration = MutableLiveData<Pair<Long, Long>>()
    val selectedParameters = MutableLiveData<List<String>>()

    val parameters: LiveData<List<ParameterItem>>
    val entries = Transformations.switchMap(duration) {
        entryRepository.observeEntriesWithin(it.first, it.second)
    }
    val graphs = MediatorLiveData<List<Graph>>()
    private val templates = templateRepository.observeTemplates()
    private val entriesAndTemplates = MediatorLiveData<Pair<List<Entry>?, List<Template>?>>()



    init {
        entriesAndTemplates.addSource(entries, this::combineEntriesAndTemplates)
        entriesAndTemplates.addSource(templates, this::combineEntriesAndTemplates)

        parameters = Transformations.map(entriesAndTemplates) {
            val entries = it.first ?: emptyList()
            val temps = it.second ?: emptyList()
            val parameterItems = mutableListOf<ParameterItem>()

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

            parameterItems.add(ParameterItem("sleep",
                "Sleep",
                Graph(xMin,
                    xMax,
                    0,
                    13,
                    mutableMapOf(),
                    Color.parseColor("#8DCAD4")),
                noTimeStart, noTimeEnd
            ))
            parameterItems.add(ParameterItem("spots",
                "New spots",
                Graph(xMin,
                    xMax,
                    0,
                    20,
                    mutableMapOf(),
                    Color.parseColor("#8DCAD4")),
                noTimeStart, noTimeEnd
            ))
            parameterItems.add(ParameterItem("ratings",
                "Skin ratings",
                Graph(xMin,
                    xMax,
                    0,
                    10,
                    mutableMapOf(),
                    Color.parseColor("#8DCAD4")),
                noTimeStart, noTimeEnd
            ))
            parameterItems.add(ParameterItem("mood",
                "Mood",
                Graph(xMin,
                    xMax,
                    0,
                    10,
                    mutableMapOf(),
                    Color.parseColor("#8DCAD4")),
                noTimeStart, noTimeEnd
            ))

            temps.forEachIndexed { index, template ->
                parameterItems.add(ParameterItem(template.timestamp.toString(),
                    template.label,
                    Graph(xMin,
                        xMax,
                        template.min,
                        template.max,
                        mutableMapOf(),
                        Color.parseColor("#8DCAD4")),
                    noTimeStart, noTimeEnd
                ))
            }
            parameterItems.forEach { paramValues[it.id] = it.graph.valuesMap }

            entries.forEachIndexed { i, entry ->
                val date = entry.day
                val x = daysBetween(noTimeStart, date.time)
                paramValues["sleep"]?.put(x, entry.sleep)
                paramValues["spots"]?.put(x, entry.newSpots)
                paramValues["ratings"]?.put(x, entry.rating)
                paramValues["mood"]?.put(x, entry.mood)

                entry.templateValues.map {
                    paramValues[it.key]?.put(x, it.value)
                }
            }
            parameterItems
        }


        graphs.addSource(selectedParameters, this::combineParameters)
        graphs.addSource(parameters, this::combineParameters)
    }

    private fun combineEntriesAndTemplates(any: Any) {
        entriesAndTemplates.value = Pair(entries.value, templates.value)
    }

    private fun combineParameters(any: Any) {
        val paramValues = parameters.value
        val selectedParamValues = selectedParameters.value
        if (paramValues != null && selectedParamValues != null) {
            val graphValues = mutableListOf<Graph>()
            paramValues.forEach {
                if (selectedParamValues.contains(it.id)) graphValues.add(it.graph)
            }
            graphs.value = graphValues
        }
    }

    private fun daysBetween(start: Long, end: Long): Int {
        return ((end - start) / (24 * 60 * 60 * 1000)).toInt()
    }


}