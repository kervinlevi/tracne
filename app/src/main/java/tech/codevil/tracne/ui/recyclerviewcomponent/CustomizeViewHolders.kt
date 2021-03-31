package tech.codevil.tracne.ui.recyclerviewcomponent

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.ViewHolderAddTemplateBinding

/**
 * Created by kervin.decena on 31/03/2021.
 */
class CustomizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class CustomizeAddViewHolder(itemBinding: ViewHolderAddTemplateBinding, listener: Listener) :
    RecyclerView.ViewHolder(itemBinding.root) {

    interface Listener {
        fun onClickAddCustomQuestion()
    }

    init {
        itemView.setOnClickListener { listener.onClickAddCustomQuestion() }
    }

}