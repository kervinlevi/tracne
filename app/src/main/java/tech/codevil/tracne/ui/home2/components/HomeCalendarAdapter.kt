package tech.codevil.tracne.ui.home2.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.ViewHolderDateHomeBinding

class HomeCalendarAdapter(private val listener: Listener) :
    ListAdapter<HomeCalendar, HomeCalendarViewHolder>(HomeCalendarDiffCallback) {

    interface Listener : HomeCalendarViewHolder.Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCalendarViewHolder {
        val binding = ViewHolderDateHomeBinding.inflate(LayoutInflater.from(parent.context))
        return HomeCalendarViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: HomeCalendarViewHolder, position: Int) {
        holder.setCalendar(getItem(position))
    }

    fun setCalendarList(calendarList: List<HomeCalendar>) {
        submitList(calendarList)
    }

    fun findTodayItemPosition(): Int {
        return currentList.indexOfFirst { homeCalendar -> homeCalendar.isToday }
    }
}