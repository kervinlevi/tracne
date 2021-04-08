package tech.codevil.tracne.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import tech.codevil.tracne.databinding.ViewSliderTemplateBinding
import tech.codevil.tracne.model.Template

/**
 * Created by kervin.decena on 08/04/2021.
 */
class SliderTemplateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val binding = ViewSliderTemplateBinding.inflate(LayoutInflater.from(context), this)

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
        binding.questionSliderTemplate.text = template.guidingQuestion
        binding.barSliderTemplate.setMarks(template.max)
    }

    private fun attachCallback() {
        binding.barSliderTemplate.setOnValueChangedListener(object : FancySeekBar.Listener {
            override fun onValueChanged(newValue: Int) {
                callback?.onValueChanged(template?.timestamp!!, newValue)
            }
        })
    }

    private fun detachCallback() {
        binding.barSliderTemplate.setOnValueChangedListener(null)
    }

}