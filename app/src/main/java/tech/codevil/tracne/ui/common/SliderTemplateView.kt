package tech.codevil.tracne.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import tech.codevil.tracne.R
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachCallback()
        clipChildren = false
        clipToPadding = false
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
        binding.barSliderTemplate.setMinMax(template.min, template.max)
        binding.barSliderTemplate.setLabels(template.valuesLabel)
    }

    fun setValue(value: Int) {
        binding.barSliderTemplate.setValue(value)
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

    fun showError(error: String?) {
        binding.errorTextSliderTemplate.text = error
        binding.errorTextSliderTemplate.visibility = VISIBLE
    }

    fun hideError() {
        binding.errorTextSliderTemplate.visibility = INVISIBLE
    }

}