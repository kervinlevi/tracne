package tech.codevil.tracne.ui.recyclerviewcomponent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.databinding.ViewHolderAddTemplateBinding

/**
 * Created by kervin.decena on 31/03/2021.
 */
class CustomizeAdapter(val listener: Listener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Listener: CustomizeAddViewHolder.Listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ViewHolderAddTemplateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomizeAddViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1
    }
}