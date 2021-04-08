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
interface TemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TemplateCacheEntity): Long

    @Query("SELECT * FROM template")
    suspend fun get(): List<TemplateCacheEntity>

    @Query("SELECT * FROM template")
    fun observe(): LiveData<List<TemplateCacheEntity>>
}