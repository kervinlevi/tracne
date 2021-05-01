package tech.codevil.tracne.ui.statistics

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tech.codevil.tracne.model.Template

@Parcelize
data class Parameter( //TODO: make this template toggle, include template object
    val template: Template,
    var isEnabled: Boolean = true,
    var isChecked: Boolean = false
) : Parcelable {
    fun toggle() {
        isChecked = !isChecked
    }
}