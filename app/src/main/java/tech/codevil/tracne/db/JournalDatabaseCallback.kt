package tech.codevil.tracne.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import tech.codevil.tracne.common.util.Constants.TEMPLATE_STATUS_ACTIVE
import tech.codevil.tracne.common.util.Constants.TEMPLATE_TYPE_SLIDER

object JournalDatabaseCallback: RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val gson = Gson()
        val sleep = ContentValues().apply {
            put("timestamp", 1619839373133L)
            put("label", "Sleep")
            put("guiding_question", "How long did you sleep?")
            put("type", TEMPLATE_TYPE_SLIDER)
            put("min", 0)
            put("max", 13)
            put("values_label_json", gson.toJson(emptyMap<Int, String>()).toString())
            put("status", TEMPLATE_STATUS_ACTIVE)
        }

        val mood = ContentValues().apply {
            put("timestamp", 1619839373134L)
            put("label", "Mood")
            put("guiding_question", "How are you feeling today?")
            put("type", TEMPLATE_TYPE_SLIDER)
            put("min", 1)
            put("max", 5)
            put("values_label_json", gson.toJson(emptyMap<Int, String>()).toString())
            put("status", TEMPLATE_STATUS_ACTIVE)
        }

        val newSpots = ContentValues().apply {
            put("timestamp", 1619839373135L)
            put("label", "New spots")
            put("guiding_question", "How many new spots do you notice?")
            put("type", TEMPLATE_TYPE_SLIDER)
            put("min", 0)
            put("max", 20)
            put("values_label_json", gson.toJson(mapOf(20 to "20+")).toString())
            put("status", TEMPLATE_STATUS_ACTIVE)
        }

        val skinRatings = ContentValues().apply {
            put("timestamp", 1619839373136L)
            put("label", "Skin ratings")
            put("guiding_question", "How would you rate your skin right now?")
            put("type", TEMPLATE_TYPE_SLIDER)
            put("min", 1)
            put("max", 10)
            put("values_label_json", gson.toJson(emptyMap<Int, String>()).toString())
            put("status", TEMPLATE_STATUS_ACTIVE)
        }

        db.insert("template", CONFLICT_IGNORE, mood)
        db.insert("template", CONFLICT_IGNORE, sleep)
        db.insert("template", CONFLICT_IGNORE, newSpots)
        db.insert("template", CONFLICT_IGNORE, skinRatings)
    }

}