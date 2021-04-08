package tech.codevil.tracne.db

import tech.codevil.tracne.common.util.EntityMapper
import tech.codevil.tracne.model.Template
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
class TemplateCacheMapper @Inject constructor() : EntityMapper<Template, TemplateCacheEntity> {


    override fun mapFromEntity(entity: Template): TemplateCacheEntity {
        return TemplateCacheEntity(
            timestamp = entity.timestamp,
            label = entity.label,
            guidingQuestion = entity.guidingQuestion,
            type = entity.type,
            min = entity.min,
            max = entity.max,
            status = entity.status
        )
    }

    override fun mapToEntity(domainModel: TemplateCacheEntity): Template {
        return Template(
            timestamp = domainModel.timestamp,
            label = domainModel.label,
            guidingQuestion = domainModel.guidingQuestion,
            type = domainModel.type,
            min = domainModel.min,
            max = domainModel.max,
            status = domainModel.status
        )
    }

    fun mapFromEntities(entities: List<TemplateCacheEntity>): List<Template> {
        return entities.map { mapToEntity(it) }
    }
}