package tech.codevil.tracne.ui.viewmodel


import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.model.Question
import tech.codevil.tracne.repository.QuestionRepository
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
@HiltViewModel
class AddQuestionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _minMaxVisible = MutableLiveData<Boolean>()
    val minMaxVisible: LiveData<Boolean> get() = _minMaxVisible

    private val _addSuccess = MutableLiveData<DataState<Unit>>()
    val addSuccess: LiveData<DataState<Unit>> get() = _addSuccess

    init {
        _minMaxVisible.value = false
    }

    fun onTypeSelected(type: String) {
        _minMaxVisible.value = when (type) {
            Constants.QUESTION_TYPE_YES_NO -> false
            Constants.QUESTION_TYPE_NUMERIC, Constants.QUESTION_TYPE_SLIDER -> true
            else -> false
        }
    }

    fun addQuestion(
        label: String,
        guidingQuestion: String,
        type: String,
        min: String,
        max: String
    ) {
        viewModelScope.launch {
            //TODO: form validation
            val question = Question(
                timestamp = System.currentTimeMillis(),
                label = label,
                guidingQuestion = guidingQuestion,
                type = type,
                min = Integer.parseInt(min),
                max = Integer.parseInt(max),
                status = Constants.QUESTION_STATUS_ACTIVE
            )
            _addSuccess.value = DataState.Loading
            val id = questionRepository.insertQuestion(question)
            val dataState =
                if (id == -1L) DataState.Error(RuntimeException("error inserting"))
                else DataState.Success(Unit)
            _addSuccess.value = dataState
        }
    }

}