package tech.codevil.tracne.ui.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.repository.EntryRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository
) : ViewModel() {

    private val _entries: LiveData<List<Entry>> = entryRepository.getEntriesLiveData()
    val entries: LiveData<List<Entry>> get() = _entries

    private val _date: MutableLiveData<String> = MutableLiveData()
    val date: LiveData<String> = _date

    val enableWritingToday: LiveData<Boolean>

    init {
        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        _date.value = format.format(Calendar.getInstance().time)

        enableWritingToday = Transformations.map(entries) {
            if (it.isNotEmpty()) format.format(it.last().timestamp) != date.value else false
        }
    }

    fun insertEntry(entry: Entry) = viewModelScope.launch {
        entryRepository.insertEntry(entry)
    }

}