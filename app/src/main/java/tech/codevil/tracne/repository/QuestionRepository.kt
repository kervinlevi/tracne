package tech.codevil.tracne.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import tech.codevil.tracne.db.QuestionCacheMapper
import tech.codevil.tracne.db.QuestionDao
import tech.codevil.tracne.model.Question
import javax.inject.Inject

/**
 * Created by kervin.decena on 06/04/2021.
 */
class QuestionRepository @Inject constructor(
    private val questionDao: QuestionDao,
    private val questionCacheMapper: QuestionCacheMapper
) {

    suspend fun insertQuestion(question: Question) =
        questionDao.insert(questionCacheMapper.mapFromEntity(question))

    fun observeQuestions(): LiveData<List<Question>> {
        return Transformations.map(questionDao.observe(), questionCacheMapper::mapFromEntities)
    }
}