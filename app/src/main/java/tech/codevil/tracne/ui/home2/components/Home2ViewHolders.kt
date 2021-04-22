package tech.codevil.tracne.ui.home2.components

import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.*

class GreetingsViewHolder(
    private val itemBinding: ViewHolderGreetingsBinding,
    private val listener: Listener,
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    interface Listener {

    }

    fun setWritingEnabled(enabled: Boolean) {
        itemBinding.writeCardHome.isClickable = enabled
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

    var param: ParameterItem? = null

    init {
        itemView.setOnClickListener { listener.onParameterClicked(param) }
    }

    fun setParameterItem(parameter: ParameterItem?) {
        if (parameter != null) {
            itemBinding.labelParameter.text = parameter.label
            itemBinding.graphViewParameter.setGraphs(listOf(parameter.graph))
            param = parameter
        } else {
            itemBinding.labelParameter.text = ""
            itemBinding.graphViewParameter.clearGraphs()
        }
        itemBinding.graphViewParameter.postInvalidate()
    }

    interface Listener {
        fun onParameterClicked(parameter: ParameterItem?)
    }

}