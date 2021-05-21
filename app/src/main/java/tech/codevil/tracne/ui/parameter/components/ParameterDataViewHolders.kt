package tech.codevil.tracne.ui.parameter.components

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.common.util.Constants
import tech.codevil.tracne.databinding.ViewHolderDataBinding
import tech.codevil.tracne.databinding.ViewHolderDataHeaderBinding
import tech.codevil.tracne.model.Entry
import tech.codevil.tracne.model.Template

class ParameterDataViewHolder(private val binding: ViewHolderDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun present(entry: Entry, template1: Template?, template2: Template?) {
        binding.dateData.text = Constants.DAY_FORMAT.format(entry.day)

        if (template1 == null) {
            binding.mainData.visibility = View.INVISIBLE
        } else {
            binding.mainData.visibility = View.VISIBLE
            binding.mainData.text = ""
            entry.values.get(template1.id())?.let { value ->
                binding.mainData.text = template1.getLabelOf(value)
            }
        }

        if (template2 == null) {
            binding.compareData.visibility = View.INVISIBLE
        } else {
            binding.compareData.visibility = View.VISIBLE
            binding.compareData.text = ""
            entry.values.get(template2.id())?.let { value ->
                binding.compareData.text = template2.getLabelOf(value)
            }
        }
    }

}

class ParameterDataHeaderViewHolder(private val binding: ViewHolderDataHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun present(template1: Template?, template2: Template?) {
        binding.mainData.visibility = if (template1 == null) View.INVISIBLE else View.VISIBLE
        binding.mainData.text = template1?.label

        binding.compareData.visibility = if (template2 == null) View.INVISIBLE else View.VISIBLE
        binding.compareData.text = template2?.label
    }

}