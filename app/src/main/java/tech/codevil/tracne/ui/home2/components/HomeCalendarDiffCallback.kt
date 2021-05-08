package tech.codevil.tracne.ui.home2.components

import androidx.recyclerview.widget.DiffUtil

object HomeCalendarDiffCallback: DiffUtil.ItemCallback<HomeCalendar>() {
    override fun areItemsTheSame(oldItem: HomeCalendar, newItem: HomeCalendar): Boolean {
        return oldItem.dayOfMonth == newItem.dayOfMonth && oldItem.dayOfWeek == newItem.dayOfWeek
    }

    override fun areContentsTheSame(oldItem: HomeCalendar, newItem: HomeCalendar): Boolean {
        return oldItem.isChecked == newItem.isChecked
                && oldItem.isToday == newItem.isToday
                && oldItem.isFutureDate == newItem.isFutureDate
    }
}