package tech.codevil.tracne.ui.recyclerviewcomponent

import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.R
import tech.codevil.tracne.databinding.ViewHolderAddTemplateBinding
import tech.codevil.tracne.databinding.ViewHolderDisplayTemplateBinding
import tech.codevil.tracne.model.Question

/**
 * Created by kervin.decena on 31/03/2021.
 */
class CustomizeViewHolder(
    val itemBinding: ViewHolderDisplayTemplateBinding,
    val listener: Listener
) :
    RecyclerView.ViewHolder(itemBinding.root) {
    interface Listener {

    }

    fun setTemplate(question: Question) {
        itemBinding.labelDisplayTemplate.text = question.label
        itemBinding.guidingQuestionDisplayTemplate.text = question.guidingQuestion
        itemBinding.typeTextDisplayTemplate.text =
            itemView.context.getString(R.string.format_template_type, question.type)
    }

}

class CustomizeAddViewHolder(itemBinding: ViewHolderAddTemplateBinding, val listener: Listener) :
    RecyclerView.ViewHolder(itemBinding.root) {

    interface Listener {
        fun onClickAddCustomQuestion()
    }

    init {
        itemView.setOnClickListener { listener.onClickAddCustomQuestion() }
    }

}