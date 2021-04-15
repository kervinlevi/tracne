package tech.codevil.tracne.ui.statistics

import android.graphics.Color
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.ui.statistics.MultipleGraphView.Graph
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(entryRepository: EntryRepository): ViewModel() {

    val timestampStart = MutableLiveData<Long>()
    val timestampEnd = MutableLiveData<Long>()
    val timestampLiveData = MediatorLiveData<Pair<Long, Long>>()
    val entriesWithinTimestamp: LiveData<List<Entry>>
    val graphsFromEntries: LiveData<List<Graph>>
    val selectedParameters = MutableLiveData<List<String>>()

    init {
        timestampLiveData.addSource(timestampStart, this::combineTimestamp)
        timestampLiveData.addSource(timestampEnd, this::combineTimestamp)

        entriesWithinTimestamp = Transformations.switchMap(timestampLiveData) {
            entryRepository.observeEntriesWithin(it.first, it.second)
        }
        graphsFromEntries = Transformations.map(entriesWithinTimestamp) { entries ->
            val parameters = selectedParameters.value ?: listOf<String>()
            val graph = mutableListOf<Graph>()
            val listValuesMap = mutableListOf<MutableMap<Int, Int>>().apply {
                parameters.map { add(mutableMapOf()) }
            }
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestampStart.value ?: 0L
            val xMin = calendar.get(Calendar.DAY_OF_MONTH)
            calendar.timeInMillis = timestampEnd.value ?: 0L
            val xMax = calendar.get(Calendar.DAY_OF_MONTH)

            entries.map {
                calendar.timeInMillis = it.timestamp
                val x = calendar.get(Calendar.DAY_OF_MONTH)
                parameters.forEachIndexed { index, param ->
                    when(param) {
                        "sleep" -> listValuesMap[index][x] = it.sleep
                        "spots" -> listValuesMap[index][x] = it.newSpots
                        "ratings" -> listValuesMap[index][x] = it.rating
                        "mood" -> listValuesMap[index][x] = it.mood
                    }
                }

            }
            parameters.forEachIndexed { index, param ->
                when(param) {
                    "sleep" -> graph.add(Graph(xMin, xMax, yMin = 0, yMax = 13, listValuesMap[index], Color.MAGENTA))
                    "spots" -> graph.add(Graph(xMin, xMax, yMin = 0, yMax = 20, listValuesMap[index], Color.BLUE))
                    "ratings" -> graph.add(Graph(xMin, xMax, yMin = 0, yMax = 10, listValuesMap[index], Color.GREEN))
                    "mood" -> graph.add(Graph(xMin, xMax, yMin = 0, yMax = 10, listValuesMap[index], Color.YELLOW))
                }
            }
            graph
        }

        selectedParameters.value = listOf("sleep", "spots")
        timestampStart.value = 1617600886554L
        timestampEnd.value = 1618810486554L
    }

    private fun combineTimestamp(timestamp: Long) {
        if (timestampStart.value != null && timestampEnd.value != null) {
            timestampLiveData.value = Pair(timestampStart.value!!, timestampEnd.value!!)
        }
    }


}