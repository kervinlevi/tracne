package tech.codevil.tracne.db

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tech.codevil.tracne.common.util.EntityMapper
import tech.codevil.tracne.model.Template
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
class TemplateCacheMapper @Inject constructor(private val gson: Gson) :
    EntityMapper<TemplateCacheEntity, Template> {

    private val valuesType = object : TypeToken<Map<Int, String>>() {}.type

    override fun mapFromEntity(entity: TemplateCacheEntity): Template {
        Log.d(javaClass.simpleName, entity.toString())
        return Template(
            timestamp = entity.timestamp,
            label = entity.label,
            guidingQuestion = entity.guidingQuestion,
            type = entity.type,
            min = entity.min,
            max = entity.max,
            status = entity.status,
            valuesLabel = gson.fromJson(entity.valuesLabelJson, valuesType),
        )
    }

    override fun mapToEntity(domainModel: Template): TemplateCacheEntity {
        return TemplateCacheEntity(
            timestamp = domainModel.timestamp,
            label = domainModel.label,
            guidingQuestion = domainModel.guidingQuestion,
            type = domainModel.type,
            min = domainModel.min,
            max = domainModel.max,
            status = domainModel.status,
            valuesLabelJson = gson.toJson(domainModel.valuesLabel).toString()
        )
    }

    fun mapFromEntities(entities: List<TemplateCacheEntity>): List<Template> {
        return entities.map { mapFromEntity(it) }
    }
}