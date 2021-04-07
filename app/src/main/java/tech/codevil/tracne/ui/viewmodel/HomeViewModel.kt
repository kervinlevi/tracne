package tech.codevil.tracne.ui.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.repository.EntryRepository
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _entries: LiveData<List<Entry>> = entryRepository.getEntriesLiveData()
    val entries: LiveData<List<Entry>> get() = _entries

    val enableWritingToday: LiveData<Boolean>

    init {
        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        enableWritingToday = Transformations.map(entries) {
            val today = format.format(Calendar.getInstance().time)
            if (it.isNotEmpty()) format.format(it.last().timestamp) != today else true
        }
    }

}