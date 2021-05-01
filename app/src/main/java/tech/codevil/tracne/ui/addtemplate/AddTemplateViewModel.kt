package tech.codevil.tracne.ui.addtemplate


import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.common.util.Constants.TEMPLATE_TYPE_SLIDER
import tech.codevil.tracne.common.util.Constants.TEMPLATE_TYPE_YES_NO
import tech.codevil.tracne.common.util.DataState
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.TemplateRepository
import tech.codevil.tracne.ui.common.EditTextState
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
@HiltViewModel
class AddTemplateViewModel @Inject constructor(
    private val templateRepository: TemplateRepository
) : ViewModel() {

    private val _addSuccess = MutableLiveData<DataState<Unit>>()
    val addSuccess: LiveData<DataState<Unit>> get() = _addSuccess

    val labelInput: MutableLiveData<String> = MutableLiveData()
    private val _labelState: LiveData<EditTextState>
    val labelState: LiveData<EditTextState> get() = _labelState

    val guidingQuestionInput: MutableLiveData<String> = MutableLiveData()
    private val _guidingQuestionState: LiveData<EditTextState>
    val guidingQuestionState: LiveData<EditTextState> get() = _guidingQuestionState

    val typeInput: MutableLiveData<String> = MutableLiveData()
    private val _typeState: LiveData<EditTextState>
    val typeState: LiveData<EditTextState> get() = _typeState

    private val _minMaxVisible = MutableLiveData<Boolean>()
    val minMaxVisible: LiveData<Boolean> get() = _minMaxVisible

    val minInput: MutableLiveData<String> = MutableLiveData()
    private val _minState: LiveData<EditTextState>
    val minState: LiveData<EditTextState> get() = _minState

    val maxInput: MutableLiveData<String> = MutableLiveData()
    private val _maxState: LiveData<EditTextState>
    val maxState: LiveData<EditTextState> get() = _maxState

    init {
        _minMaxVisible.value = false

        _labelState = Transformations.map(labelInput, this::validateLabel)
        _guidingQuestionState =
            Transformations.map(guidingQuestionInput, this::validateGuidingQuestion)
        _typeState = Transformations.map(typeInput) {
            _minMaxVisible.value = (it == TEMPLATE_TYPE_SLIDER)
            validateType(it)
        }
        _minState = Transformations.map(minInput, this::validateMin)
        _maxState = Transformations.map(maxInput, this::validateMax)
    }

    fun onAddTemplateClicked() {
        viewModelScope.launch {
            _addSuccess.value = DataState.Loading

            labelInput.value = labelInput.value
            val labelInputState = labelState.value

            guidingQuestionInput.value = guidingQuestionInput.value
            val guidingQuestionInputState = guidingQuestionState.value

            typeInput.value = typeInput.value
            val typeInputState = typeState.value

            minInput.value = if (typeInput.value == TEMPLATE_TYPE_SLIDER) minInput.value else "0"
            val minInputState = minState.value

            maxInput.value = if (typeInput.value == TEMPLATE_TYPE_SLIDER) maxInput.value else "1"
            val maxInputState = maxState.value

            val valuesLabel = if (typeInput.value == TEMPLATE_TYPE_YES_NO) mapOf(0 to "No",
                1 to "Yes") else emptyMap()

            val hasInputError = labelInputState?.hasError == true
                    || guidingQuestionInputState?.hasError == true
                    || typeInputState?.hasError == true
                    || minInputState?.hasError == true
                    || maxInputState?.hasError == true

            if (hasInputError) {
                _addSuccess.value = DataState.Error(RuntimeException("Check error"))
            } else {
                val template = Template(
                    timestamp = System.currentTimeMillis(),
                    label = labelInput.value!!,
                    guidingQuestion = guidingQuestionInput.value!!,
                    type = typeInput.value!!,
                    min = Integer.parseInt(minInput.value!!),
                    max = Integer.parseInt(maxInput.value!!),
                    status = Constants.TEMPLATE_STATUS_ACTIVE,
                    valuesLabel = valuesLabel
                )
                val id = templateRepository.insertTemplate(template)
                val dataState = if (id == -1L) DataState.Error(RuntimeException("error inserting"))
                else DataState.Success(Unit)
                _addSuccess.value = dataState
            }
        }
    }

    private fun validateLabel(label: String?): EditTextState {
        val hasError = label.isNullOrBlank()
        val error = if (hasError) "Label cannot be blank." else null
        return EditTextState(hasError, error)
    }

    private fun validateGuidingQuestion(guidingQuestion: String?): EditTextState {
        val hasError = guidingQuestion.isNullOrBlank()
        val error = if (hasError) "Guiding question cannot be blank." else null
        return EditTextState(hasError, error)
    }

    private fun validateType(type: String?): EditTextState {
        val hasError = type.isNullOrBlank()
        val error = if (hasError) "Select a type" else null
        return EditTextState(hasError, error)
    }

    //TODO: min against max validation
    private fun validateMin(min: String?): EditTextState {
        val minimum = min?.toIntOrNull()
        val hasError = minimum == null || minimum < 0
        val error = if (hasError) "Min should be greater than or equal to 0" else null
        return EditTextState(hasError, error)
    }

    private fun validateMax(max: String?): EditTextState {
        val maximum = max?.toIntOrNull()
        val hasError = maximum == null || maximum > 100
        val error = if (hasError) "Max should be less than or equal 100" else null
        return EditTextState(hasError, error)
    }

}