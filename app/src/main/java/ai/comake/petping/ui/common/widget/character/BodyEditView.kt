package ai.comake.petping.ui.common.widget.character

import ai.comake.petping.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

/**
 * android-petping-2
 * Class: BodyEditView
 * Created by cliff on 2022/03/24.
 *
 * Description: 캐릭터 바디 커스텀 뷰
 */
class BodyEditView @JvmOverloads constructor(
    context: Context,
    initialColor: Int,
    private val listener: ChangeBodyListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var isShowColor = false
    private var selectedColor = -1
    private val colorImageViewArray: ArrayList<View> = ArrayList()

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_character_body, this, true)
        initView()

        setColor(initialColor)
    }

    private fun initView() {
        colorImageViewArray.add(findViewById<View>(R.id.color_1))
        colorImageViewArray.add(findViewById<View>(R.id.color_2))
        colorImageViewArray.add(findViewById<View>(R.id.color_3))
        colorImageViewArray.add(findViewById<View>(R.id.color_4))
        colorImageViewArray.add(findViewById<View>(R.id.color_5))
        colorImageViewArray.add(findViewById<View>(R.id.color_6))
        colorImageViewArray.add(findViewById<View>(R.id.color_7))
        colorImageViewArray.add(findViewById<View>(R.id.color_8))
        colorImageViewArray.add(findViewById<View>(R.id.color_9))
        colorImageViewArray.add(findViewById<View>(R.id.color_10))
        colorImageViewArray.add(findViewById<View>(R.id.color_11))
        colorImageViewArray.add(findViewById<View>(R.id.color_12))
        colorImageViewArray.add(findViewById<View>(R.id.color_13))
        colorImageViewArray.add(findViewById<View>(R.id.color_14))
        colorImageViewArray.add(findViewById<View>(R.id.color_15))
        colorImageViewArray.add(findViewById<View>(R.id.color_16))
        colorImageViewArray.add(findViewById<View>(R.id.color_17))
        colorImageViewArray.add(findViewById<View>(R.id.color_18))

        colorImageViewArray.forEach { it.setOnClickListener(this) }

        findViewById<View>(R.id.color_more).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.color_more -> {
                isShowColor = true
                findViewById<View>(R.id.color_more).visibility = View.GONE
                findViewById<View>(R.id.color_group).visibility = View.VISIBLE
                findViewById<View>(R.id.color_12).visibility = View.VISIBLE
            }
            R.id.color_1 -> setColor(0)
            R.id.color_2 -> setColor(1)
            R.id.color_3 -> setColor(2)
            R.id.color_4 -> setColor(3)
            R.id.color_5 -> setColor(4)
            R.id.color_6 -> setColor(5)
            R.id.color_7 -> setColor(6)
            R.id.color_8 -> setColor(7)
            R.id.color_9 -> setColor(8)
            R.id.color_10 -> setColor(9)
            R.id.color_11 -> setColor(10)
            R.id.color_12 -> setColor(11)
            R.id.color_13 -> setColor(12)
            R.id.color_14 -> setColor(13)
            R.id.color_15 -> setColor(14)
            R.id.color_16 -> setColor(15)
            R.id.color_17 -> setColor(16)
            R.id.color_18 -> setColor(17)
        }
    }

    private fun setColor(id: Int) {
        selectedColor = if (selectedColor == id) -1 else id

        colorImageViewArray.forEach {
            it.setBackgroundResource(0)
        }

        if (selectedColor != -1) {
            colorImageViewArray[id].setBackgroundResource(R.drawable.sel_circle)
        }

        listener.onColorChanged(selectedColor)
    }

    interface ChangeBodyListener {
        fun onColorChanged(colorIndex: Int)
    }
}