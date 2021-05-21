package tech.codevil.tracne.ui.parameter.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.ViewHolderDataBinding
import tech.codevil.tracne.databinding.ViewHolderDataHeaderBinding
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template

class ParameterDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    private val entryList = mutableListOf<Entry>()
    private var template1: Template? = null
    private var template2: Template? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_HEADER) {
            return ParameterDataHeaderViewHolder(ViewHolderDataHeaderBinding.inflate(inflater,
                parent,
                false))
        } else {
            return ParameterDataViewHolder(ViewHolderDataBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ParameterDataHeaderViewHolder -> {
                holder.present(template1, template2)
            }
            is ParameterDataViewHolder -> {
                holder.present(entryList[position - 1], template1, template2)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (entryList.isEmpty()) 0 else entryList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (entryList.isNotEmpty() && position == 0) TYPE_HEADER else TYPE_ITEM
    }

    fun setEntries(entries: List<Entry>) {
        if (entryList != entries) {
            entryList.clear()
            entryList.addAll(entries)
            notifyDataSetChanged()
        }
    }

    fun setTemplate1(temp1: Template?) {
        if (template1 != temp1) {
            template1 = temp1
            notifyDataSetChanged()
        }
    }

    fun setTemplate2(temp2: Template?) {
        if (template2 != temp2) {
            template2 = temp2
            notifyDataSetChanged()
        }
    }
}