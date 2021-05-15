package tech.codevil.tracne.ui.home2.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.ViewHolderCalendarPickerBinding
import tech.codevil.tracne.databinding.ViewHolderGreetingsBinding
import tech.codevil.tracne.databinding.ViewHolderOptionsBinding
import tech.codevil.tracne.databinding.ViewHolderParameterBinding

class Home2Adapter(private val listener: Listener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_GREETINGS = 0
        const val ITEM_TYPE_OPTIONS = 1
        const val ITEM_TYPE_CALENDAR_PICKER = 2
        const val ITEM_TYPE_PARAMETER = 3
    }

    interface Listener : GreetingsViewHolder.Listener, OptionsViewHolder.Listener,
        CalendarPickerViewHolder.Listener, ParametersViewHolder.Listener

    var writingTodayEnabled = true
        set(value: Boolean) {
            if (field != value) {
                field = value
                notifyItemChanged(0)
            }
        }

    var date = ""
        set(value: String) {
            if (field != value) {
                field = value
                notifyItemChanged(2)
            }
        }

    var isWeekly = true
        set(value: Boolean) {
            if (field != value) {
                field = value
                notifyItemChanged(2)
            }
        }

    var calendarList = listOf<HomeCalendar>()
        set(value) {
            if (field != value) {
                field = value
                notifyItemChanged(0)
            }
        }

    val parameters = mutableListOf<TemplateGraph>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_GREETINGS -> {
                val binding = ViewHolderGreetingsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GreetingsViewHolder(binding, listener)
            }
            ITEM_TYPE_OPTIONS -> {
                val binding = ViewHolderOptionsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OptionsViewHolder(binding, listener)
            }
            ITEM_TYPE_CALENDAR_PICKER -> {
                val binding = ViewHolderCalendarPickerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CalendarPickerViewHolder(binding, listener)
            }
            else -> {
                val binding = ViewHolderParameterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                ParametersViewHolder(binding, listener)
            }


        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_GREETINGS -> {
                (holder as? GreetingsViewHolder)?.let {
                    holder.setWritingEnabled(writingTodayEnabled)
                    holder.setHomeCalendarList(calendarList)
                }
            }
            ITEM_TYPE_CALENDAR_PICKER -> {
                (holder as? CalendarPickerViewHolder)?.let {
                    holder.setDate(date)
                    holder.setIsWeekly(isWeekly)
                }
            }
            ITEM_TYPE_PARAMETER -> {
                (holder as? ParametersViewHolder)?.let {
                    holder.setParameterItem(getParameter(position))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 3 + parameters.size
    }

    private fun getParameter(position: Int): TemplateGraph? {
        val index = position - 3
        return parameters.getOrNull(index)
    }

    fun setParameterItems(templateGraphs: List<TemplateGraph>) {
        val oldSize = parameters.size
        parameters.clear()
        parameters.addAll(templateGraphs)
        notifyItemRangeChanged(3, maxOf(oldSize, templateGraphs.size))
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_TYPE_GREETINGS
            1 -> ITEM_TYPE_OPTIONS
            2 -> ITEM_TYPE_CALENDAR_PICKER
            else -> ITEM_TYPE_PARAMETER
        }
    }
}