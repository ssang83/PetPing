package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.util.dpToPixels
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class MaxHeightRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    )

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    //MeasureSpec.AT_MOST 부모가 정한 최대 길이까지(max) wrap
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mHeightMeasureSpec =
            MeasureSpec.makeMeasureSpec(415.dpToPixels(context).toInt(), MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }
}