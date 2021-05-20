package tech.codevil.tracne.common.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Created by kervin.decena on 09/04/2021.
 */
object Extensions {

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun EditText.textWatcherFlow(): Flow<CharSequence?> = callbackFlow<CharSequence?> {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                sendBlocking(s)
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose {
            removeTextChangedListener(textWatcher)
        }
    }
    
    fun Calendar.setMinTime() {
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.AM_PM, 0)
    }

    fun Calendar.setMaxTime() {
        set(Calendar.HOUR, getActualMaximum(Calendar.HOUR))
        set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
        set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
        set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
        set(Calendar.AM_PM, getActualMaximum(Calendar.AM_PM))
    }

    fun Float.sp(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            this,
            context.resources.displayMetrics)
    }

    fun Float.dp(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics)
    }

}