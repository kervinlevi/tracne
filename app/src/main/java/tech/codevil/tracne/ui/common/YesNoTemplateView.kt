package tech.codevil.tracne.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import tech.codevil.tracne.R
import tech.codevil.tracne.databinding.ViewYesNoTemplateBinding
import tech.codevil.tracne.model.Template

/**
 * Created by kervin.decena on 08/04/2021.
 */
class YesNoTemplateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewYesNoTemplateBinding.inflate(LayoutInflater.from(context), this)

    private var template: Template? = null
    private var callback: TemplateViewCallback? = null
    private var currentValue = -1

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachCallback()
    }

    override fun onDetachedFromWindow() {
        detachCallback()
        super.onDetachedFromWindow()
    }

    fun setCallback(templateViewCallback: TemplateViewCallback) {
        callback = templateViewCallback
        attachCallback()
    }

    fun setTemplate(template: Template) {
        this.template = template
        binding.questionYesNoTemplate.text = template.guidingQuestion
    }

    private fun attachCallback() {
        if (callback != null) {
            binding.radioGroupYesNoTemplate.setOnCheckedChangeListener { _, checkedId ->
                val newValue = if (checkedId == binding.positiveRadioYesNoTemplate.id) 1 else 0
                if (newValue != currentValue) {
                    currentValue = newValue
                    callback?.onValueChanged(template?.timestamp!!, currentValue)
                }
            }
        }
    }

    private fun detachCallback() {
        binding.radioGroupYesNoTemplate.setOnCheckedChangeListener(null)
    }

}