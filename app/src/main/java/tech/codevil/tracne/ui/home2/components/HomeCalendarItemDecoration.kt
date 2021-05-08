package tech.codevil.tracne.ui.home2.components

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class HomeCalendarItemDecoration(
    private val mDividerSize: Int,
    private val mPaddingStart: Int,
    private val mPaddingEnd: Int,
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, recyclerView: RecyclerView,
        state: RecyclerView.State,
    ) {
        val itemPosition = recyclerView.getChildAdapterPosition(view)
        //compute the item offsets for first item
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        } else if (itemPosition == 0) {
            outRect.left = mPaddingStart
        }

        //compute item offset for middle items
        if (recyclerView.adapter != null && itemPosition == recyclerView.adapter!!.itemCount - 1 && itemPosition != 0) {
            outRect.right = mPaddingEnd
        } else {
            outRect.right = mDividerSize
        }
    }
}
