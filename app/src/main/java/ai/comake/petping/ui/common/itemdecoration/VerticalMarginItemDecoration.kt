package ai.comake.petping.ui.common.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: VerticalMarginItemDecoration
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
class VerticalMarginItemDecoration(
    @IntRange(from = 0) private val marginTop: Int = 0,
    @IntRange(from = 0) private val marginBottom: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemCount = parent.adapter?.itemCount?:0
        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.top = marginTop
                outRect.bottom = (marginBottom / 2)
            }
            (itemCount - 1) -> {
                outRect.top = (marginBottom / 2)
                outRect.bottom = marginBottom
            }
            else -> {
                outRect.top = (marginBottom / 2)
                outRect.bottom = (marginBottom / 2)
            }
        }
    }
}