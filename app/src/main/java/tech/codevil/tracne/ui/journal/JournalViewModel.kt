package tech.codevil.tracne.ui.journal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants.DAY_FORMAT
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

    val templates: LiveData<List<Template>> get() = templateRepository.observeTemplates()
    val values = MutableLiveData<Map<String, Int>>()

    private val _showError = MutableLiveData<List<String>>()
    val showError: LiveData<List<String>> get() = _showError

    private val _date: MutableLiveData<String> = MutableLiveData()
    val date: LiveData<String> get() = _date

    init {
        _date.value = DAY_FORMAT.format(Calendar.getInstance().time)
    }


    fun insertEntry() = viewModelScope.launch {
        _insertSuccess.value = DataState.Loading
        templateRepository.getTemplates().collect { templates ->
            val values = values.value ?: emptyMap()

            val showErrorOn = mutableListOf<String>()
            Log.d(javaClass.simpleName, "templates $templates")
            templates.forEach {
                Log.d(javaClass.simpleName, "templates.forEach $it")
                if (!values.containsKey(it.id())) {
                    showErrorOn.add(it.id())
                    Log.d(javaClass.simpleName, "showErrorOn.add $it")
                }
            }
            _showError.value = showErrorOn
            if (showErrorOn.isEmpty()) {
                val entry = Entry(
                    timestamp = System.currentTimeMillis(),
                    day = Date(),
                    values = values,
                    lastUpdated = System.currentTimeMillis())
                val id = entryRepository.insertEntry(entry)
                val dataState =
                    if (id == -1L) DataState.Error(RuntimeException("error inserting"))
                    else DataState.Success(Unit)
                _insertSuccess.value = dataState
            }
            else {
                _insertSuccess.value =  DataState.Error(RuntimeException("incomplete values"))
            }
        }
    }

}