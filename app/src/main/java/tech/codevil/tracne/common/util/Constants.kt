package tech.codevil.tracne.common.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kervin.decena on 06/04/2021.
 */
object Constants {

    const val TEMPLATE_TYPE_YES_NO = "yesno"
    const val TEMPLATE_TYPE_NUMERIC = "numeric"
    const val TEMPLATE_TYPE_SLIDER = "slider"

    const val TEMPLATE_STATUS_ACTIVE = "active"
    const val TEMPLATE_STATUS_ARCHIVED = "archived"

    val DAY_FORMAT = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
    val RANGE_FORMAT = SimpleDateFormat("MMM. d", Locale.ENGLISH)
}