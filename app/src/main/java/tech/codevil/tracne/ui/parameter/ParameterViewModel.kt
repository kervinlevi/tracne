package tech.codevil.tracne.ui.parameter

import android.graphics.Color
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.repository.TemplateRepository
import tech.codevil.tracne.ui.home2.components.TemplateGraph
import tech.codevil.tracne.ui.statistics.MultipleGraphView.Graph
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ParameterViewModel @Inject constructor(
    entryRepository: EntryRepository,
    templateRepository: TemplateRepository,
) : ViewModel() {

    val parameter = MutableLiveData<TemplateGraph>()
    val duration = MutableLiveData<Pair<Long, Long>>()
    val selectedParameters = MutableLiveData<List<String>>()

    val parameters: LiveData<List<TemplateGraph>>
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
            val parameterItems = mutableListOf<TemplateGraph>()
            val thisParameter = parameter.value

            val start = duration.value?.first ?: 0L
            val end = duration.value?.second ?: 0L

            if (start == 0L || end == 0L || thisParameter == null) {
                return@map emptyList()
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = start
            calendar.setMinTime()
            val noTimeStart = calendar.timeInMillis
            val xMin = 0

            calendar.timeInMillis = end
            calendar.setMaxTime()
            val noTimeEnd = calendar.timeInMillis
            val xMax = daysBetween(noTimeStart, noTimeEnd)


            val paramValues = mutableMapOf<String, MutableMap<Int, Int>>()
            var selectedParam: TemplateGraph? = null
            temps.forEachIndexed { index, template ->
                if (template.id() != thisParameter.template.id()) {
                    val graph = Graph(xMin,
                        xMax,
                        template.min,
                        template.max,
                        mutableMapOf(),
                        Color.parseColor("#8DCAD4"))
                    parameterItems.add(TemplateGraph(template, graph, noTimeStart, noTimeEnd))
                    paramValues[template.id()] = graph.valuesMap
                }
                else {
                    val graph = Graph(xMin,
                        xMax,
                        template.min,
                        template.max,
                        mutableMapOf(),
                        Color.parseColor("#8DCAD4"))
                    selectedParam = TemplateGraph(template, graph, noTimeStart, noTimeEnd)
                    paramValues[template.id()] = graph.valuesMap
                }

            }

            entries.forEachIndexed { i, entry ->
                val date = entry.day
                val x = daysBetween(noTimeStart, date.time)
                entry.values.map { pair -> paramValues[pair.key]?.put(x, pair.value) }
            }
            selectedParam?.let { param -> parameter.value = param }
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
            val selectedParameterGraph = parameter.value?.graph
            if (selectedParameterGraph != null) {
                graphValues.add(selectedParameterGraph)
            }
            paramValues.forEach {
                if (selectedParamValues.contains(it.template.id())) graphValues.add(it.graph)
            }
            graphs.value = graphValues
        }
    }

    private fun daysBetween(start: Long, end: Long): Int {
        return ((end - start) / (24 * 60 * 60 * 1000)).toInt()
    }


}