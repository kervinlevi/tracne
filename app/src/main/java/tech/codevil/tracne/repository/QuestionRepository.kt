package tech.codevil.tracne.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import tech.codevil.tracne.db.TemplateCacheMapper
import tech.codevil.tracne.db.TemplateDao
import tech.codevil.tracne.model.Template
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
class QuestionRepository @Inject constructor(
    private val templateDao: TemplateDao,
    private val templateCacheMapper: TemplateCacheMapper
) {

    suspend fun insertQuestion(template: Template) =
        templateDao.insert(templateCacheMapper.mapFromEntity(template))

    fun observeQuestions(): LiveData<List<Template>> {
        return Transformations.map(templateDao.observe(), templateCacheMapper::mapFromEntities)
    }
}