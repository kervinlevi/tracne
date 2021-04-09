package tech.codevil.tracne.ui.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*

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
//        .buffer(Channel.CONFLATED)
//        .distinctUntilChanged()
//        .debounce(300L)

}