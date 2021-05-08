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

    @Query("SELECT * FROM entry ORDER BY time_created")
    suspend fun get(): List<EntryCacheEntity>

    @Query("SELECT * FROM entry WHERE time_created BETWEEN :start AND :end ORDER BY time_created")
    suspend fun get(start: Long, end: Long): List<EntryCacheEntity>

    @Query("SELECT * FROM entry ORDER BY time_created")
    fun observe(): LiveData<List<EntryCacheEntity>>

    @Query("SELECT * FROM entry WHERE day = :day ORDER BY time_created")
    fun observeByDay(day: String): LiveData<List<EntryCacheEntity>>

    @Query("SELECT * FROM entry WHERE time_created BETWEEN :start AND :end ORDER BY time_created")
    fun getWithinTimestamp(start: Long, end: Long): LiveData<List<EntryCacheEntity>>
}