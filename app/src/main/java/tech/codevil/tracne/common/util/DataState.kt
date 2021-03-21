package tech.codevil.tracne.common.util

import java.lang.Exception

/**
 * Created by kervin.decena on 21/03/2021.
 */
sealed class DataState<out T> {

    data class Success<out U>(val data: U): DataState<U>()

    data class Error(val exception: Exception): DataState<Nothing>()

    object Loading: DataState<Nothing>()

}