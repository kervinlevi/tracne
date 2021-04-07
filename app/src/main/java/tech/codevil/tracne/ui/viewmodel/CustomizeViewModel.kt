package tech.codevil.tracne.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.model.Question
import tech.codevil.tracne.repository.QuestionRepository
import javax.inject.Inject

/**
 * Created by kervin.decena on 07/04/2021.
 */
@HiltViewModel
class CustomizeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    questionRepository: QuestionRepository
): ViewModel() {

    private val _questions = questionRepository.observeQuestions()
    val questions: LiveData<List<Question>> = _questions

}