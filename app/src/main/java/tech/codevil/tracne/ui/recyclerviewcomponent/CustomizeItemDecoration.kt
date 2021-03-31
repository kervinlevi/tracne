package tech.codevil.tracne.ui.recyclerviewcomponent

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import tech.codevil.tracne.R

/**
 * Created by kervin.decena on 31/03/2021.
 */
class CustomizeItemDecoration: RecyclerView.ItemDecoration() {

    var paddingTop: Int? = null
    var paddingBottom: Int? = null
    var paddingStart: Int? = null
    var paddingEnd: Int? = null

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = getPaddingTop(view.context)
        }
        if (position > 0 && position == parent.childCount - 1) {
            outRect.bottom = getPaddingBottom(view.context)
        }
        if (position != RecyclerView.NO_POSITION) {
            outRect.left = getPaddingStart(view.context)
            outRect.right = getPaddingEnd(view.context)
        }
    }

    private fun getPaddingTop(context: Context): Int {
        if (paddingTop == null) {
            paddingTop = context.resources.getDimensionPixelSize(R.dimen.customize_item_decoration_top_padding)
        }
        return paddingTop!!
    }

    private fun getPaddingBottom(context: Context): Int {
        if (paddingBottom == null) {
            paddingBottom = context.resources.getDimensionPixelSize(R.dimen.customize_item_decoration_bottom_padding)
        }
        return paddingBottom!!
    }

    private fun getPaddingStart(context: Context): Int {
        if (paddingStart == null) {
            paddingStart = context.resources.getDimensionPixelSize(R.dimen.customize_item_decoration_start_padding)
        }
        return paddingStart!!
    }

    private fun getPaddingEnd(context: Context): Int {
        if (paddingEnd == null) {
            paddingEnd = context.resources.getDimensionPixelSize(R.dimen.customize_item_decoration_end_padding)
        }
        return paddingEnd!!
    }
}