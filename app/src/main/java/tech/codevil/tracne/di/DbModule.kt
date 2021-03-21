package tech.codevil.tracne.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tech.codevil.tracne.db.EntryDao
import tech.codevil.tracne.db.JournalDatabase
import javax.inject.Singleton

/**
 * Created by kervin.decena on 21/03/2021.
 */

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideJournalDatabase(@ApplicationContext context: Context): JournalDatabase {
        return Room.databaseBuilder(
            context,
            JournalDatabase::class.java,
            JournalDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideEntryDao(journalDatabase: JournalDatabase): EntryDao {
        return journalDatabase.entryDao()
    }
}