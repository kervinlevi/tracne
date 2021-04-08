package tech.codevil.tracne.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tech.codevil.tracne.common.util.EntityMapper
import tech.codevil.tracne.model.Entry
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
class EntryCacheMapper @Inject constructor(private val gson: Gson) :
    EntityMapper<EntryCacheEntity, Entry> {

    private val type = object: TypeToken<Map<String, Any>>() {}.type

    override fun mapFromEntity(entity: EntryCacheEntity): Entry {
        return Entry(
            timestamp = entity.timestamp,
            mood = entity.mood,
            sleep = entity.sleep,
            newSpots = entity.newSpots,
            rating = entity.rating,
            templateValues = gson.fromJson(entity.templateValuesJson, type)
        )
    }

    override fun mapToEntity(domainModel: Entry): EntryCacheEntity {
        return EntryCacheEntity(
            timestamp = domainModel.timestamp,
            mood = domainModel.mood,
            sleep = domainModel.sleep,
            newSpots = domainModel.newSpots,
            rating = domainModel.rating,
            templateValuesJson = gson.toJson(domainModel.templateValues).toString()
        )
    }

    fun mapFromEntities(entities: List<EntryCacheEntity>): List<Entry> {
        return entities.map { mapFromEntity(it) }
    }
}