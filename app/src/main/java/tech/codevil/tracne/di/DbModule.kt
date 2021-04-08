package tech.codevil.tracne.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tech.codevil.tracne.db.EntryDao
import tech.codevil.tracne.db.JournalDatabase
import tech.codevil.tracne.db.TemplateDao
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
        ).addMigrations().build()
    }

    @Singleton
    @Provides
    fun provideEntryDao(journalDatabase: JournalDatabase): EntryDao {
        return journalDatabase.entryDao()
    }

    @Singleton
    @Provides
    fun provideQuestionDao(journalDatabase: JournalDatabase): TemplateDao {
        return journalDatabase.questionDao()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }
}