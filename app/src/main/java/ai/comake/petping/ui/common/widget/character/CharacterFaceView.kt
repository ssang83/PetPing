package ai.comake.petping.ui.common.widget.character

import ai.comake.petping.R
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * android-petping-2
 * Class: CharacterFaceView
 * Created by cliff on 2022/03/23.
 *
 * Description: 캐릭터 얼굴 커스텀 뷰
 */
class CharacterFaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
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
        LayoutInflater.from(context).inflate(R.layout.layout_character_face_view, this, true)

        typeList.add(findViewById(R.id.type_1))
        typeList.add(findViewById(R.id.type_2))
        typeList.add(findViewById(R.id.type_3))

        colorList.add(findViewById(R.id.pet_body))

        val type1List:ArrayList<View> = ArrayList()
        type1List.add(findViewById(R.id.type_a_muzzle_a))
        type1List.add(findViewById(R.id.type_b_muzzle_a))
        type1List.add(findViewById(R.id.type_c_muzzle_a))

        val type2List:ArrayList<View> = ArrayList()
        type2List.add(findViewById(R.id.type_a_eyebrow))
        type2List.add(findViewById(R.id.type_b_eyebrow))
        type2List.add(findViewById(R.id.type_c_eyebrow))

        val type3List:ArrayList<View> = ArrayList()
        type3List.add(findViewById(R.id.type_a_muzzle_a))
        type3List.add(findViewById(R.id.type_b_muzzle_a))
        type3List.add(findViewById(R.id.type_c_muzzle_a))
        type3List.add(findViewById(R.id.type_a_spotted))
        type3List.add(findViewById(R.id.type_b_spotted))
        type3List.add(findViewById(R.id.type_c_spotted))

        patternTypeList.add(type1List)
        patternTypeList.add(type2List)
        patternTypeList.add(type3List)

        patternColorList.add(findViewById(R.id.type_a_muzzle_a))
        patternColorList.add(findViewById(R.id.type_b_muzzle_a))
        patternColorList.add(findViewById(R.id.type_c_muzzle_a))
        patternColorList.add(findViewById(R.id.type_a_eyebrow))
        patternColorList.add(findViewById(R.id.type_b_eyebrow))
        patternColorList.add(findViewById(R.id.type_c_eyebrow))

        patternColorList.add(findViewById(R.id.type_a_spotted))
        patternColorList.add(findViewById(R.id.type_b_spotted))
        patternColorList.add(findViewById(R.id.type_c_spotted))

        bodyColorList.add(findViewById(R.id.pet_a_half))
        bodyColorList.add(findViewById(R.id.pet_b_half))
        bodyColorList.add(findViewById(R.id.pet_c_half))

        bodyColorList.forEach {
            it.visibility = View.GONE
        }
    }

    fun updateType(index: Int) {
        type = index
        typeList.forEach { it.visibility = LinearLayout.GONE }
        typeList[index].visibility = View.VISIBLE

    }

    fun updateColor(index: Int) {
        colorIndex = index
        findViewById<ImageView>(R.id.pet_body).setColorFilter(color[index])
    }

    fun updateColor(color: String) {
        findViewById<ImageView>(R.id.pet_body).setColorFilter(Color.parseColor(color))
    }

    fun updatePattern(index: Int) {
        pattern = index
        patternTypeList.forEach { it.forEach { view -> view.visibility = View.GONE } }
        patternTypeList[index].forEach { it.visibility = View.VISIBLE }
    }

    fun updatePatternColor(index: Int) {
        patternColor = index
        patternColorList.forEach { it.setColorFilter(color[index]) }
    }

    fun updatePatternColor(color: String) {
        patternColorList.forEach { it.setColorFilter(Color.parseColor(color)) }
    }

    fun updateBodyColor(index: Int) {
        bodyColor = index
        if(index == -1) {
            bodyColorList.forEach { it.visibility = View.GONE }
        } else {
            bodyColorList.forEach {
                it.visibility = View.VISIBLE
                it.setColorFilter(color[index])
            }
        }
    }

    fun updateBodyColor(color: String) {
        bodyColorList.forEach {
            it.visibility = View.VISIBLE
            it.setColorFilter(Color.parseColor(color))
        }
    }
}