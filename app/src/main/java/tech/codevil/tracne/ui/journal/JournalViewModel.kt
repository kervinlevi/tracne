package tech.codevil.tracne.ui.journal

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants.DAY_FORMAT
import tech.codevil.tracne.common.util.Constants.DAY_NO_YEAR_FORMAT
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.EntryRepository
import tech.codevil.tracne.repository.TemplateRepository
import java.util.*
import javax.inject.Inject

/**
 * Created by kervin.decena on 07/04/2021.
 */
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val templateRepository: TemplateRepository,
) : ViewModel() {

    private val _insertSuccess = MutableLiveData<DataState<Unit>>()
    val insertSuccess: LiveData<DataState<Unit>> get() = _insertSuccess

    val templates = templateRepository.observeTemplates()

    val templatesAndInitialEntry = MediatorLiveData<Pair<List<Template>, Entry?>>()
    val values = MutableLiveData<Map<String, Int>>()

    private val _showError = MutableLiveData<Map<String, String>>()
    val showError: LiveData<Map<String, String>> get() = _showError

    val timestamp = MutableLiveData<Long>()

    val date: LiveData<String> = Transformations.map(timestamp) { timestamp ->
        val calendar = Calendar.getInstance()
        val targetDate = Calendar.getInstance().apply { timeInMillis = timestamp }

        when {
            DAY_FORMAT.format(calendar.timeInMillis) == DAY_FORMAT.format(timestamp) -> "Today"
            calendar.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) -> DAY_NO_YEAR_FORMAT.format(
                timestamp)
            else -> DAY_FORMAT.format(timestamp)
        }
    }

    val initialValue = Transformations.switchMap(timestamp) {
        entryRepository.getEntryByDayLiveData(DAY_FORMAT.format(Date(it)))
    }

    init {
        templatesAndInitialEntry.addSource(templates, this::mergeForTemplates)
        templatesAndInitialEntry.addSource(initialValue, this::mergeForTemplates)
    }

    fun mergeForTemplates(any: Any?) {
        templates.value?.let { templates ->
            templatesAndInitialEntry.value = Pair(templates, initialValue.value)
        }
    }

    fun insertEntry() = viewModelScope.launch {
        _insertSuccess.value = DataState.Loading
        templateRepository.getTemplates().collect { templates ->
            val values = values.value ?: emptyMap()

            val showErrorOn = mutableMapOf<String, String>()
            Log.d(javaClass.simpleName, "templates $templates")
            templates.forEach {
                Log.d(javaClass.simpleName, "templates.forEach $it")
                if (!values.containsKey(it.id())) {
                    showErrorOn[it.id()] = "Required field."
                    Log.d(javaClass.simpleName, "showErrorOn.add $it")
                }
            }
            _showError.value = showErrorOn
            if (showErrorOn.isEmpty()) {
                val timestamp = timestamp.value ?: System.currentTimeMillis()
                val day = Date(timestamp)
                val entry = Entry(
                    timestamp = timestamp,
                    day = day,
                    values = values,
                    lastUpdated = System.currentTimeMillis())
                val id = entryRepository.insertEntry(entry)
                val dataState =
                    if (id == -1L) DataState.Error(RuntimeException("error inserting"))
                    else DataState.Success(Unit)
                _insertSuccess.value = dataState
            } else {
                _insertSuccess.value = DataState.Error(RuntimeException("incomplete values"))
            }
        }
    }

}