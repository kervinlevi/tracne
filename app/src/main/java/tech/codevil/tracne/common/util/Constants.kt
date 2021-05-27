package tech.codevil.tracne.common.util

import android.view.animation.OvershootInterpolator
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
    val DAY_NO_YEAR_FORMAT = SimpleDateFormat("MMMM d", Locale.ENGLISH)
    val RANGE_FORMAT = SimpleDateFormat("MMM d", Locale.ENGLISH)
    val SHORTENED_DAY_FORMAT = SimpleDateFormat("MMM d", Locale.ENGLISH)

    val MONTHS_SHORTENED = mapOf(
        0 to "Jan", 1 to "Feb", 2 to "Mar", 3 to "Apr", 4 to "May", 5 to "Jun", 6 to "Jul",
        7 to "Aug", 8 to "Sep", 9 to "Oct.", 10 to "Nov", 11 to "Dec"
    )

    val OVERSHOOT_INTERPOLATOR by lazy { OvershootInterpolator() }
}