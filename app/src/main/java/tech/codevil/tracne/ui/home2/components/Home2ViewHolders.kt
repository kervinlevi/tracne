package tech.codevil.tracne.ui.home2.components

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.R
import tech.codevil.tracne.databinding.*

class GreetingsViewHolder(
    private val itemBinding: ViewHolderGreetingsBinding,
    private val listener: Listener,
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    interface Listener : HomeCalendarAdapter.Listener {
        fun onWriteClicked()
    }

    private val calendarAdapter: HomeCalendarAdapter

    init {
        itemBinding.writeCardHome.setOnClickListener { listener.onWriteClicked() }
        calendarAdapter = HomeCalendarAdapter(listener)
        val dp8 = itemView.resources.getDimensionPixelSize(R.dimen.dp_8)
        val dp16 = itemView.resources.getDimensionPixelSize(R.dimen.dp_16)
        itemBinding.calendarRecyclerView.apply {
            adapter = calendarAdapter
            val manager = object: LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false) {
                override fun onLayoutCompleted(state: RecyclerView.State?) {
                    super.onLayoutCompleted(state)
                    val position = calendarAdapter.findTodayItemPosition()
                    if (position != -1) {
                        smoothScrollToPosition(position)
                    }
                }
            }
            layoutManager = manager
            addItemDecoration(HomeCalendarItemDecoration(dp8, dp16, dp16))
        }
    }

    fun setWritingEnabled(enabled: Boolean) {
        itemBinding.writeCardHome.isClickable = true

    }
    fun setHomeCalendarList(calendarList: List<HomeCalendar>) {
        calendarAdapter.setCalendarList(calendarList)
    }

}

class OptionsViewHolder(itemBinding: ViewHolderOptionsBinding, private val listener: Listener) :
    RecyclerView.ViewHolder(itemBinding.root) {

    interface Listener {
        fun onCalendarOptionClicked()
        fun onUserOptionClicked()
        fun onTemplateOptionClicked()
        fun onAboutOptionClicked()
    }

    init {
        itemBinding.calendarOptions.setOnClickListener { listener.onCalendarOptionClicked() }
        itemBinding.userOptions.setOnClickListener { listener.onUserOptionClicked() }
        itemBinding.templatesOptions.setOnClickListener { listener.onTemplateOptionClicked() }
        itemBinding.aboutOptions.setOnClickListener { listener.onAboutOptionClicked() }
    }

}


class CalendarPickerViewHolder(
    private val itemBinding: ViewHolderCalendarPickerBinding,
    private val listener: Listener,
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun setDate(date: String) {
        itemBinding.datePickerLabel.text = date
    }

    interface Listener {
        fun onDatePickerClicked()
    }

    init {
        itemBinding.datePickerLabel.setOnClickListener {
            listener.onDatePickerClicked()
        }
    }

}

class ParametersViewHolder(
    private val itemBinding: ViewHolderParameterBinding,
    private val listener: Listener,
) :
    RecyclerView.ViewHolder(itemBinding.root) {

    var param: TemplateGraph? = null

    init {
        itemView.setOnClickListener { listener.onParameterClicked(param) }
    }

    fun setParameterItem(parameter: TemplateGraph?) {
        if (parameter != null) {
            itemBinding.labelParameter.text = parameter.template.label
            itemBinding.graphViewParameter.setGraphs(listOf(parameter.graph))
            param = parameter
        } else {
            itemBinding.labelParameter.text = ""
            itemBinding.graphViewParameter.clearGraphs()
        }
        itemBinding.graphViewParameter.postInvalidate()
    }

    interface Listener {
        fun onParameterClicked(parameter: TemplateGraph?)
    }

}