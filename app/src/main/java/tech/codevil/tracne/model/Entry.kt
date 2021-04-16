package tech.codevil.tracne.model

import java.util.Date

/**
 * Created by kervin.decena on 21/03/2021.
 */
data class Entry(
    var timestamp: Long,
    var day: Date,
    var mood: Int,
    var sleep: Int,
    var newSpots: Int,
    var rating: Int,
    var templateValues: Map<String, Int>,
    var lastUpdated: Long
)