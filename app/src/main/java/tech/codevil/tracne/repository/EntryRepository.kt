package tech.codevil.tracne.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import tech.codevil.tracne.db.EntryCacheMapper
import tech.codevil.tracne.db.EntryDao
import tech.codevil.tracne.model.Entry
import java.util.*
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao,
    private val entryCacheMapper: EntryCacheMapper
) {

    suspend fun insertEntry(entry: Entry) = entryDao.insert(entryCacheMapper.mapToEntity(entry))

    suspend fun getEntries(): Flow<List<Entry>> = flow {
        val entryEntities = entryDao.get()
        emit(entryCacheMapper.mapFromEntities(entryEntities))
    }

    fun getEntriesLiveData(): LiveData<List<Entry>> =
        Transformations.map(entryDao.observe(), entryCacheMapper::mapFromEntities)

    fun observeEntriesWithin(start: Long, end: Long): LiveData<List<Entry>> =
        Transformations.map(
            entryDao.getWithinTimestamp(start, end),
            entryCacheMapper::mapFromEntities
        )

    suspend fun loadMockData() {
        getEntries().collect {
            if (it.isEmpty()) {

                val calendar = Calendar.getInstance()
                for (i in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    calendar.set(Calendar.DAY_OF_MONTH, i)

                    val entry = Entry(
                        timestamp = calendar.timeInMillis,
                        day = calendar.time,
                        mood = (1..9).random(),
                        sleep = (1..12).random(),
                        newSpots = (1..19).random(),
                        rating = (4..9).random(),
                        templateValues = mapOf(),
                        lastUpdated = calendar.timeInMillis
                    )
                    insertEntry(entry)
                }
            }
        }


    }

}