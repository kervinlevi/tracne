package tech.codevil.tracne.ui.statistics

import android.graphics.Color
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.common.util.Extensions.setMaxTime
import tech.codevil.tracne.common.util.Extensions.setMinTime
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.ui.statistics.MultipleGraphView.Graph
import java.util.*
import java.util.Calendar.DAY_OF_MONTH
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(entryRepository: EntryRepository) : ViewModel() {

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
                parameters.forEachIndexed { index, param ->
                    when (param) {
                        "sleep" -> listValuesMap[index][x] = it.sleep
                        "spots" -> listValuesMap[index][x] = it.newSpots
                        "ratings" -> listValuesMap[index][x] = it.rating
                        "mood" -> listValuesMap[index][x] = it.mood
                    }
                }

            }
            parameters.forEachIndexed { index, param ->
                when (param) {
                    "sleep" -> graph.add(
                        Graph(
                            xMin,
                            xMax,
                            yMin = 0,
                            yMax = 13,
                            listValuesMap[index],
                            Color.MAGENTA
                        )
                    )
                    "spots" -> graph.add(
                        Graph(
                            xMin,
                            xMax,
                            yMin = 0,
                            yMax = 20,
                            listValuesMap[index],
                            Color.BLUE
                        )
                    )
                    "ratings" -> graph.add(
                        Graph(
                            xMin,
                            xMax,
                            yMin = 0,
                            yMax = 10,
                            listValuesMap[index],
                            Color.GREEN
                        )
                    )
                    "mood" -> graph.add(
                        Graph(
                            xMin,
                            xMax,
                            yMin = 0,
                            yMax = 10,
                            listValuesMap[index],
                            Color.YELLOW
                        )
                    )
                }
            }
            graph
        }

        selectedParameters.value = listOf("sleep", "spots")

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

    private fun combineTimestamp(timestamp: Long) {
        if (timestampStart.value != null && timestampEnd.value != null) {
            timestampLiveData.value = Pair(timestampStart.value!!, timestampEnd.value!!)
        }
    }

    private fun daysBetween(start: Long, end: Long): Int {
        return ((end - start) / (24 * 60 * 60 * 1000)).toInt()
    }

}