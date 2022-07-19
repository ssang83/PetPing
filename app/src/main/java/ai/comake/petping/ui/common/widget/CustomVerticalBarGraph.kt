package ai.comake.petping.ui.common.widget

import ai.comake.petping.data.vo.MonthlyCount
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

/**
 * android-petping-2
 * Class: CustomVerticalBarGraph
 * Created by cliff on 2022/03/02.
 *
 * Description:
 */
class CustomVerticalBarGraph @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var data = listOf(4, 5, 6, 1, 2, 3, 0, 0, 0, 1, 2, 3)
    private var highlight = 0
    private var monthlyCountList = listOf(MonthlyCount(3,4))

    override fun onDraw(canvas: Canvas) {
        val marginDp = 7
        val margin = marginDp * resources.displayMetrics.density
        val barWidth = (width - (data.size - 1) * margin) / data.size
        val paint = Paint()
        paint.isAntiAlias = true
        var max = (data.maxOrNull()?.toFloat()?:1f).let {
            if(it == 0f) 1f
            else it
        }

        max = if(max >= 7.0f) 7.0f else max
        val heightUnit = (70f * resources.displayMetrics.density/max).let {
            if(it > 10f*resources.displayMetrics.density)
                10f*resources.displayMetrics.density
            else
                it
        }

        data.forEachIndexed { index, value ->
            var graphVal = value
            if (highlight == index) {
                paint.color = Color.parseColor("#ff4857")
            } else {
                paint.color = Color.parseColor("#ececec")
            }

            canvas.drawCircle(
                barWidth / 2 + index * (barWidth + margin),
                100f * resources.displayMetrics.density,
                barWidth / 2,
                paint
            )

            if(graphVal >= 7) graphVal = 7
            canvas.drawRect(
                index * (barWidth + margin),
                100f * resources.displayMetrics.density - (graphVal * heightUnit),
                barWidth + index * (barWidth + margin),
                100f * resources.displayMetrics.density,
                paint
            )
            canvas.drawCircle(
                barWidth / 2 + index * (barWidth + margin),
                100f * resources.displayMetrics.density - graphVal * heightUnit,
                barWidth / 2,
                paint
            )

//            if (value != 0) {
//                if (value >= 7) {
//                    canvas.drawRect(
//                        index * (barWidth + margin),
//                        100f * resources.displayMetrics.density - (max * heightUnit),
//                        barWidth + index * (barWidth + margin),
//                        100f * resources.displayMetrics.density,
//                        paint
//                    )
//                    canvas.drawCircle(
//                        barWidth / 2 + index * (barWidth + margin),
//                        100f * resources.displayMetrics.density - max * heightUnit,
//                        barWidth / 2,
//                        paint
//                    )
//                } else {
//                    val unit = max / 7.0f
//                    canvas.drawRect(
//                        index * (barWidth + margin),
//                        100f * resources.displayMetrics.density - (value.toFloat() * unit * heightUnit),
//                        barWidth + index * (barWidth + margin),
//                        100f * resources.displayMetrics.density,
//                        paint
//                    )
//                    canvas.drawCircle(
//                        barWidth / 2 + index * (barWidth + margin),
//                        100f * resources.displayMetrics.density - (value.toFloat() * unit * heightUnit),
//                        barWidth / 2,
//                        paint
//                    )
//                }
//            }
        }

        val textPaint = Paint()
        textPaint.color = Color.parseColor("#777777")
        textPaint.textAlign = Paint.Align.CENTER
        val typeface = Typeface.createFromAsset(resources.assets, "nanum_square_round.ttf")
        textPaint.typeface = typeface
        textPaint.textSize = 12 * resources.displayMetrics.density
        textPaint.isAntiAlias = true

        var acc = 0f
        monthlyCountList.forEach {
            val prev = acc
            acc += it.count * (barWidth + margin)
            val xPos = prev + barWidth/2 - 5.0f
            val yPos = 130f * resources.displayMetrics.density
            canvas.drawText("${it.month}월", xPos, yPos, textPaint)
        }

    }

    fun setData(data: List<Int>) {
        this.data = data
        invalidate()
    }

    fun setMonth(data: List<MonthlyCount>) {
        this.monthlyCountList = data
        invalidate()
    }

    fun setHighlight(position: Int) {
        highlight = position
        invalidate()
    }
}