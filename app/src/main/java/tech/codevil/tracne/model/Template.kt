package tech.codevil.tracne.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by kervin.decena on 06/04/2021.
 */
@Parcelize
data class Template(
    val timestamp: Long,
    val label: String,
    val guidingQuestion: String,
    val type: String,
    val min: Int,
    val max: Int,
    val valuesLabel: Map<Int, String>,
    val status: String
) : Parcelable {

    fun id() = timestamp.toString()
}