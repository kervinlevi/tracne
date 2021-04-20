package tech.codevil.tracne.ui.statistics

data class Parameter(
    val id: String,
    val label: String,
    var isEnabled: Boolean = true,
    var isChecked: Boolean = false
) {
    fun toggle() {
        isChecked = !isChecked
    }
}