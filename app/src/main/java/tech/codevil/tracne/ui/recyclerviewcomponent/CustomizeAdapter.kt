package tech.codevil.tracne.ui.recyclerviewcomponent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.ViewHolderAddTemplateBinding
import tech.codevil.tracne.databinding.ViewHolderDisplayTemplateBinding
import tech.codevil.tracne.model.Question

/**
 * Created by kervin.decena on 31/03/2021.
 */
class CustomizeAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ADD = 0
        const val TYPE_DISPLAY = 1
    }

    interface Listener: CustomizeAddViewHolder.Listener, CustomizeViewHolder.Listener

    private val items: MutableList<Question> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ADD) {
            val binding = ViewHolderAddTemplateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return CustomizeAddViewHolder(binding, listener)
        }
        val binding = ViewHolderDisplayTemplateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomizeViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_DISPLAY) {
            (holder as CustomizeViewHolder).apply { setTemplate(getItem(position)) }
        }
    }

    override fun getItemCount(): Int {
        return 1 + items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD else TYPE_DISPLAY
    }

    private fun getItem(position: Int): Question {
        return items[position - 1]
    }

    fun setItems(questions: List<Question>) {
        items.clear()
        items.addAll(questions)
        notifyDataSetChanged()
    }
}