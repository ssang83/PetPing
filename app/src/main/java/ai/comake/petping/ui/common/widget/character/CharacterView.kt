package ai.comake.petping.ui.common.widget.character

import ai.comake.petping.R
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * android-petping-2
 * Class: CharacterView
 * Created by cliff on 2022/03/23.
 *
 * Description: 캐릭터 커스텀 뷰
 */
class CharacterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var colorIndex = 0
    private var type = 0
    private var pattern = 0
    private var patternColor = 0
    private var bodyColor = 0

    private val typeList: ArrayList<View> = ArrayList()
    private val colorList: ArrayList<ImageView> = ArrayList()
    private val patternTypeList: ArrayList<ArrayList<View>> = ArrayList()
    private val patternColorList: ArrayList<ImageView> = ArrayList()
    private val bodyColorList: ArrayList<ImageView> = ArrayList()

    private val color: List<Int> = listOf(
//        Color.parseColor("#f8ddb6"),
//        Color.parseColor("#fac67d"),
//        Color.parseColor("#e9994f"),
//        Color.parseColor("#bf6a32"),
//        Color.parseColor("#864d27"),
//        Color.parseColor("#71513b"),
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
        LayoutInflater.from(context).inflate(R.layout.layout_character_view, this, true)
    }

    fun updateType(index: Int) {
        type = index
        findViewById<CharacterFaceView>(R.id.character_face_view).updateType(index)

    }

    fun updateColor(index: Int) {
        colorIndex = index
        findViewById<CharacterFaceView>(R.id.character_face_view).updateColor(index)
    }

    fun updateColor(color: String) {
        findViewById<CharacterFaceView>(R.id.character_face_view).updateColor(color)
    }

    fun updatePattern(index: Int) {
        pattern = index
        findViewById<CharacterFaceView>(R.id.character_face_view).updatePattern(index)
    }

    fun updatePatternColor(index: Int) {
        patternColor = index
        findViewById<CharacterFaceView>(R.id.character_face_view).updatePatternColor(index)
    }

    fun updatePatternColor(color: String) {
        findViewById<CharacterFaceView>(R.id.character_face_view).updatePatternColor(color)
    }

    fun updateBodyColor(index: Int) {
        bodyColor = index
        findViewById<CharacterFaceView>(R.id.character_face_view).updateBodyColor(index)
    }

    fun updateBodyColor(color: String) {
        findViewById<CharacterFaceView>(R.id.character_face_view).updateBodyColor(color)
    }
}