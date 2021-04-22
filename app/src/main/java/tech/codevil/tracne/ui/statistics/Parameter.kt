package tech.codevil.tracne.ui.statistics

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Parameter(
    val id: String,
    val label: String,
    var isEnabled: Boolean = true,
    var isChecked: Boolean = false
) : Parcelable {
    fun toggle() {
        isChecked = !isChecked
    }
}