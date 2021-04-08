package tech.codevil.tracne.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Created by kervin.decena on 21/03/2021.
 */
@Database(entities = [EntryCacheEntity::class, TemplateCacheEntity::class], version = 1)
abstract class JournalDatabase: RoomDatabase() {

    abstract fun entryDao(): EntryDao

    abstract fun questionDao(): TemplateDao

    companion object {
        val DATABASE_NAME: String = "journal_db"
    }

}