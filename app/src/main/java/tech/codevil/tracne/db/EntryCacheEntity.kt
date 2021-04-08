package tech.codevil.tracne.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by kervin.decena on 21/03/2021.
 */

@Entity(tableName = "entry", indices = [Index(value = ["timestamp"], unique = true)])
data class EntryCacheEntity(
    var timestamp: Long,
    var mood: Int,
    var sleep: Int,
    @ColumnInfo(name = "new_spots") var newSpots: Int,
    var rating: Int,
    @ColumnInfo(name = "template_values_json") var templateValuesJson: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}