package tech.codevil.tracne.db

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tech.codevil.tracne.common.util.Constants.DAY_FORMAT
import tech.codevil.tracne.common.util.EntityMapper
import tech.codevil.tracne.model.Entry
import java.util.Date
import javax.inject.Inject

/**
 * Created by kervin.decena on 21/03/2021.
 */
class EntryCacheMapper @Inject constructor(private val gson: Gson) :
    EntityMapper<EntryCacheEntity, Entry> {

    private val valuesType = object : TypeToken<Map<String, Int>>() {}.type

    override fun mapFromEntity(entity: EntryCacheEntity): Entry {
        return Entry(
            timestamp = entity.timeCreated,
            day = DAY_FORMAT.parse(entity.day) ?: Date(),
            values = gson.fromJson(entity.valuesJson, valuesType),
            lastUpdated = entity.lastUpdated
        )
    }

    override fun mapToEntity(domainModel: Entry): EntryCacheEntity {
        return EntryCacheEntity(
            timeCreated = domainModel.timestamp,
            day = DAY_FORMAT.format(domainModel.day),
            valuesJson = gson.toJson(domainModel.values).toString(),
            lastUpdated = domainModel.lastUpdated
        )
    }

    fun mapFromEntities(entities: List<EntryCacheEntity>): List<Entry> {
        return entities.map { mapFromEntity(it) }
    }
}