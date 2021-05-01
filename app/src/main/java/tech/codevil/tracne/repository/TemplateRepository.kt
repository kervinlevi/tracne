package tech.codevil.tracne.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.flow.flow
import tech.codevil.tracne.db.TemplateCacheMapper
import tech.codevil.tracne.db.TemplateDao
import tech.codevil.tracne.model.Template
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
class TemplateRepository @Inject constructor(
    private val templateDao: TemplateDao,
    private val templateCacheMapper: TemplateCacheMapper
) {

    suspend fun getTemplates() =  flow {
        val templateEntities = templateDao.get()
        emit(templateCacheMapper.mapFromEntities(templateEntities))
    }

    suspend fun insertTemplate(template: Template) =
        templateDao.insert(templateCacheMapper.mapToEntity(template))

    fun observeTemplates(): LiveData<List<Template>> {
        return Transformations.map(templateDao.observe(), templateCacheMapper::mapFromEntities)
    }
}