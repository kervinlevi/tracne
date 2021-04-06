package tech.codevil.tracne.model

/**
 * Created by kervin.decena on 06/04/2021.
 */
data class Question(
    val timestamp: Long,
    val label: String,
    val guidingQuestion: String,
    val type: String,
    val min: Int,
    val max: Int,
    val status: String
)