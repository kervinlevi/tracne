package tech.codevil.tracne.model

/**
 * Created by kervin.decena on 21/03/2021.
 */
data class Entry(
    var timestamp: Long,
    var mood: Int,
    var sleep: Int,
    var newSpots: Int,
    var rating: Int,
    var templateValues: Map<String, Any>
)