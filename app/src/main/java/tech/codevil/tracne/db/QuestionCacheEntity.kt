package tech.codevil.tracne.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by kervin.decena on 06/04/2021.
 */
@Entity(tableName = "question")
class QuestionCacheEntity(
    var timestamp: Long,
    var label: String,
    var guidingQuestion: String,
    var type: String,
    var min: Int,
    var max: Int,
    var status: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}