package tech.codevil.tracne.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tech.codevil.tracne.db.EntryCacheMapper
import tech.codevil.tracne.db.EntryDao
import tech.codevil.tracne.model.Entry
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

}