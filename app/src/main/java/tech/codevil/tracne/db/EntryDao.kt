package tech.codevil.tracne.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by kervin.decena on 21/03/2021.
 */
@Dao
interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EntryCacheEntity): Long

    @Query("SELECT * FROM entry")
    suspend fun get(): List<EntryCacheEntity>

    @Query("SELECT * FROM entry")
    fun observe(): LiveData<List<EntryCacheEntity>>
}