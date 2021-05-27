package tech.codevil.tracne.ui.home2.components

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.R
import tech.codevil.tracne.databinding.ViewHolderDateHomeBinding

class HomeCalendarViewHolder(
    private val binding: ViewHolderDateHomeBinding,
    private val listener: Listener,
) : RecyclerView.ViewHolder(binding.root) {

    interface Listener {
        fun onClickCalendar(calendar: HomeCalendar)
    }

    private var calendar: HomeCalendar? = null
    private val charcoalColor: Int by lazy {
        ResourcesCompat.getColor(itemView.resources, R.color.charcoal, null)
    }
    private val whiteColor: Int by lazy {
        ResourcesCompat.getColor(itemView.resources, R.color.white, null)
    }
    private val darkBlueColor: Int by lazy {
        ResourcesCompat.getColor(itemView.resources, R.color.dark_blue, null)
    }

    init {
        binding.root.setOnClickListener {
            calendar?.let(listener::onClickCalendar)
        }
    }

    fun setCalendar(homeCalendar: HomeCalendar) {
        binding.checkedDateHome.visibility = if (homeCalendar.isChecked) View.VISIBLE else View.GONE
        binding.dayDateHome.text = homeCalendar.dayOfWeek
        binding.numberDateHome.text = homeCalendar.dayOfMonth.toString()
        if (homeCalendar.isToday) {
            binding.root.cardElevation = 20.0f
            binding.root.setCardBackgroundColor(darkBlueColor)
            binding.numberDateHome.setTextColor(whiteColor)
            binding.dayDateHome.setTextColor(whiteColor)
            binding.dividerDateHome.setBackgroundColor(whiteColor)
            binding.checkedDateHome.setImageResource(R.drawable.ic_checked_white)
            binding.root.isClickable = true
        }
        else {
            binding.root.cardElevation = 2.0f
            binding.root.setCardBackgroundColor(whiteColor)
            binding.numberDateHome.setTextColor(charcoalColor)
            binding.dayDateHome.setTextColor(charcoalColor)
            binding.dividerDateHome.setBackgroundColor(charcoalColor)
            binding.checkedDateHome.setImageResource(R.drawable.ic_checked_3)
            binding.root.isClickable = !homeCalendar.isFutureDate
        }
        calendar = homeCalendar
    }

}