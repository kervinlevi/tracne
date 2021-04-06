package tech.codevil.tracne.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by kervin.decena on 06/04/2021.
 */
object JournalDatabaseMigration {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `question` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `timestamp` INTEGER NOT NULL, `label` TEXT NOT NULL, `guidingQuestion` TEXT NOT NULL, `type` TEXT NOT NULL, `min` INTEGER NOT NULL, `max` INTEGER NOT NULL, `status` TEXT NOT NULL)")
        }
    }

}