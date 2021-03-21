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

    val date: MutableLiveData<String> = MutableLiveData()

    val enableWritingToday: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        date.value = format.format(Calendar.getInstance().time)

        entries.observeForever { //TODO: check if it's ok to use observeForever inside view model
            if (it.isNotEmpty()) {
                enableWritingToday.value = format.format(it.last().timestamp) != date.value
            }
        }
    }

    fun insertEntry(entry: Entry) = viewModelScope.launch {
        entryRepository.insertEntry(entry)
    }

}