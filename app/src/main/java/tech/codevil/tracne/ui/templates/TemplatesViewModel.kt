package tech.codevil.tracne.ui.templates

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.QuestionRepository
import javax.inject.Inject

/**
 * Created by kervin.decena on 07/04/2021.
 */
@HiltViewModel
class TemplatesViewModel @Inject constructor(
    questionRepository: QuestionRepository
): ViewModel() {

    private val _questions = questionRepository.observeQuestions()
    val questions: LiveData<List<Template>> = _questions

}