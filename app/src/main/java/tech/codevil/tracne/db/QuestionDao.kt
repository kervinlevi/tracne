package tech.codevil.tracne.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by kervin.decena on 06/04/2021.
 */
@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: QuestionCacheEntity): Long

    @Query("SELECT * FROM question")
    suspend fun get(): List<QuestionCacheEntity>

    @Query("SELECT * FROM question")
    fun observe(): LiveData<List<QuestionCacheEntity>>
}