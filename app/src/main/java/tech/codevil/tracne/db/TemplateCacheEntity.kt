package tech.codevil.tracne.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by kervin.decena on 06/04/2021.
 */
@Entity(tableName = "template", indices = [Index(value = ["timestamp"], unique = true)])
class TemplateCacheEntity(
    var timestamp: Long,
    var label: String,
    @ColumnInfo(name = "guiding_question") var guidingQuestion: String,
    var type: String,
    var min: Int,
    var max: Int,
    @ColumnInfo(name = "values_label_json") var valuesLabelJson: String,
    var status: String,
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}