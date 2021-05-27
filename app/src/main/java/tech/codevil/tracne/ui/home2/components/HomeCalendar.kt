package tech.codevil.tracne.ui.home2.components

data class HomeCalendar(
    val timestamp: Long,
    val isChecked: Boolean,
    val dayOfWeek: String,
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isFutureDate: Boolean,
)