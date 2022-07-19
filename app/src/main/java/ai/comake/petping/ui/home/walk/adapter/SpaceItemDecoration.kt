package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.util.dpToPixels
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpaceItemDecoration
/**
 * Custom divider will be used
 */(val context: Context, resId: Int) : ItemDecoration() {
    private var divider: Drawable? = ContextCompat.getDrawable(context, resId)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + 20.dpToPixels(context)
        val right = parent.width - parent.paddingRight - 20.dpToPixels(context)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider?.intrinsicHeight!!
            divider?.setBounds(left.toInt(), top, right.toInt(), bottom)
            divider?.draw(c)
        }
    }
}

