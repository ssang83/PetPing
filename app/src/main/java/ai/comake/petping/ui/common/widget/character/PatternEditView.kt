package ai.comake.petping.ui.common.widget.character

import ai.comake.petping.R
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

/**
 * android-petping-2
 * Class: PatternEditView
 * Created by cliff on 2022/03/24.
 *
 * Description: 캐릭터 패턴 커스텀 뷰
 */
class PatternEditView @JvmOverloads constructor(
    context: Context,
    initialColor: Int,
    initialPattern: Int,
    initialBodyColor: Int,
    initialHalfColor: Int,
    initialType: Int,
    private val listener: ChangePatternListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var isShowColor = false
    private var selectedColor = initialColor
    private var selectedPattern = initialPattern

    private val colorImageViewArray: ArrayList<View> = ArrayList()
    private val patternTypeViewArray: ArrayList<View> = ArrayList()

    private val color: List<Int> = listOf(
        Color.parseColor("#f9ecd7"),
        Color.parseColor("#f7c893"),
        Color.parseColor("#f09e58"),
        Color.parseColor("#c6753f"),
        Color.parseColor("#9f653f"),
        Color.parseColor("#7c604d"),
        Color.parseColor("#ffffff"),
        Color.parseColor("#dcdcdc"),
        Color.parseColor("#ababab"),
        Color.parseColor("#737373"),
        Color.parseColor("#636363"),
        Color.parseColor("#ffee35"),
        Color.parseColor("#7ee4dd"),
        Color.parseColor("#5396fa"),
        Color.parseColor("#a181f7"),
        Color.parseColor("#f773ad"),
        Color.parseColor("#ff4857"),
        Color.parseColor("#ff7a35")
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_character_pattern, this, true)
        initView()


        setColor(initialColor)
        setType(initialPattern)

        findViewById<CharacterFaceView>(R.id.face_1).updateColor(initialBodyColor)
        findViewById<CharacterFaceView>(R.id.face_2).updateColor(initialBodyColor)
        findViewById<CharacterFaceView>(R.id.face_3).updateColor(initialBodyColor)

        findViewById<CharacterFaceView>(R.id.face_1).updateType(initialType)
        findViewById<CharacterFaceView>(R.id.face_2).updateType(initialType)
        findViewById<CharacterFaceView>(R.id.face_3).updateType(initialType)

        findViewById<CharacterFaceView>(R.id.face_1).updatePattern(0)
        findViewById<CharacterFaceView>(R.id.face_2).updatePattern(1)
        findViewById<CharacterFaceView>(R.id.face_3).updatePattern(2)

        findViewById<CharacterFaceView>(R.id.face_1).updateBodyColor(-1)
        findViewById<CharacterFaceView>(R.id.face_2).updateBodyColor(-1)
        findViewById<CharacterFaceView>(R.id.face_3).updateBodyColor(-1)
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

        patternTypeViewArray.add(findViewById(R.id.pattern_a))
        patternTypeViewArray.add(findViewById(R.id.pattern_b))
        patternTypeViewArray.add(findViewById(R.id.pattern_c))

        colorImageViewArray.forEach { it.setOnClickListener(this) }
        patternTypeViewArray.forEach { it.setOnClickListener(this) }

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
            R.id.pattern_a -> setType(0)
            R.id.pattern_b -> setType(1)
            R.id.pattern_c -> setType(2)
        }
    }

    private fun setColor(id: Int) {
        selectedColor = id

        findViewById<CharacterFaceView>(R.id.face_1).updatePatternColor(id)
        findViewById<CharacterFaceView>(R.id.face_2).updatePatternColor(id)
        findViewById<CharacterFaceView>(R.id.face_3).updatePatternColor(id)

        colorImageViewArray.forEach { it.setBackgroundResource(0) }
        colorImageViewArray[id].setBackgroundResource(R.drawable.sel_circle)

        listener.onColorChanged(id)
    }

    private fun setType(id: Int) {
        selectedPattern = id

        patternTypeViewArray.forEach { it.setBackgroundResource(0) }
        patternTypeViewArray[id].setBackgroundResource(R.drawable.sel_circle)

        listener.onPatternChanged(id)
    }

    interface ChangePatternListener {
        fun onPatternChanged(type: Int)
        fun onColorChanged(colorIndex: Int)
    }
}