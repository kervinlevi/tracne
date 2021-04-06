package tech.codevil.tracne.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by kervin.decena on 21/03/2021.
 */
@Database(entities = [EntryCacheEntity::class, QuestionCacheEntity::class], version = 2)
abstract class JournalDatabase: RoomDatabase() {

    abstract fun entryDao(): EntryDao

    abstract fun questionDao(): QuestionDao

    companion object {
        val DATABASE_NAME: String = "journal_db"
    }

}