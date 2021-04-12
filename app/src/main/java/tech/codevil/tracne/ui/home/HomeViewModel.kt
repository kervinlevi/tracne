package tech.codevil.tracne.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.common.util.Constants.DAY_FORMAT
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.repository.EntryRepository
import java.util.*
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    entryRepository: EntryRepository
) : ViewModel() {

    private val _entries: LiveData<List<Entry>> = entryRepository.getEntriesLiveData()
    val entries: LiveData<List<Entry>> get() = _entries

    val enableWritingToday: LiveData<Boolean> = Transformations.map(entries) {
        val today = DAY_FORMAT.format(Calendar.getInstance().time)
        if (it.isNotEmpty()) DAY_FORMAT.format(it.last().timestamp) != today else true
    }

}