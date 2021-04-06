package tech.codevil.tracne.db

import tech.codevil.tracne.common.util.EntityMapper
import tech.codevil.tracne.model.Question
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
class QuestionCacheMapper @Inject constructor() : EntityMapper<Question, QuestionCacheEntity> {


    override fun mapFromEntity(entity: Question): QuestionCacheEntity {
        return QuestionCacheEntity(
            timestamp = entity.timestamp,
            label = entity.label,
            guidingQuestion = entity.guidingQuestion,
            type = entity.type,
            min = entity.min,
            max = entity.max,
            status = entity.status
        )
    }

    override fun mapToEntity(domainModel: QuestionCacheEntity): Question {
        return Question(
            timestamp = domainModel.timestamp,
            label = domainModel.label,
            guidingQuestion = domainModel.guidingQuestion,
            type = domainModel.type,
            min = domainModel.min,
            max = domainModel.max,
            status = domainModel.status
        )
    }

    fun mapFromEntities(entities: List<QuestionCacheEntity>): List<Question> {
        return entities.map { mapToEntity(it) }
    }
}