package tech.codevil.tracne.ui.journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _insertSuccess = MutableLiveData<DataState<Unit>>()
    val insertSuccess: LiveData<DataState<Unit>> get() = _insertSuccess

    val templates: LiveData<List<Template>> get() = templateRepository.observeTemplates()

    private val _date: MutableLiveData<String> = MutableLiveData()
    val date: LiveData<String> get() = _date

    init {
        _date.value = DAY_FORMAT.format(Calendar.getInstance().time)
    }


    fun insertEntry(entry: Entry) = viewModelScope.launch {
        _insertSuccess.value = DataState.Loading
        val id = entryRepository.insertEntry(entry)
        val dataState =
            if (id == -1L) DataState.Error(RuntimeException("error inserting"))
            else DataState.Success(Unit)
        _insertSuccess.value = dataState
    }

}