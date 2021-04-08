package tech.codevil.tracne.ui.templates

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.repository.TemplateRepository
import javax.inject.Inject

/**
 * Created by kervin.decena on 07/04/2021.
 */
@HiltViewModel
class TemplatesViewModel @Inject constructor(
    templateRepository: TemplateRepository
): ViewModel() {

    private val _questions = templateRepository.observeTemplates()
    val questions: LiveData<List<Template>> = _questions

}