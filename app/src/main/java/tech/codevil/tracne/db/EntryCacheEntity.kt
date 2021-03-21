package tech.codevil.tracne.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by kervin.decena on 21/03/2021.
 */

@Entity(tableName = "entry")
data class EntryCacheEntity(
    var timestamp: Long,
    var mood: Int,
    var sleep: Int,
    var newSpots: Int,
    var rating: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

}