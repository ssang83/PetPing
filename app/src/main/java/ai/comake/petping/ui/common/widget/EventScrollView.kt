package ai.comake.petping.ui.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * android-petping-2
 * Class: EventScrollView
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class EventScrollView : ScrollView {
    private var scrollerTask: Runnable? = null
    private var initialPosition = 0
    private val newCheck = 100
    private var onScrollListener: OnScrollListener? = null
    fun setOnScrollListener(onScrollListener: OnScrollListener?) {
        this.onScrollListener = onScrollListener
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun init() {
        scrollerTask = Runnable {
            val newPosition = scrollY
            if (initialPosition - newPosition == 0) { //has stopped
                if (onScrollListener != null) {
                    onScrollListener!!.onScrollEnd()
                }
            } else {
                initialPosition = scrollY
                postDelayed(scrollerTask, newCheck.toLong())
            }
        }
    }

    fun startScrollerTask() {
        initialPosition = scrollY
        postDelayed(scrollerTask, newCheck.toLong())
    }

    override fun onScrollChanged(l: Int, t: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(l, t, oldX, oldY)
        if (onScrollListener != null) {
            onScrollListener!!.onScrollChanged(l, t, oldX, oldY)
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_UP -> startScrollerTask()
        }
        return super.onTouchEvent(ev)
    }

    interface OnScrollListener {
        fun onScrollChanged(l: Int, t: Int, oldX: Int, oldY: Int)
        fun onScrollEnd()
    }
}