package ai.comake.petping.ui.common.widget.character

import ai.comake.petping.R
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.get
import com.google.android.material.tabs.TabLayout

/**
 * android-petping-2
 * Class: CharacterEditView
 * Created by cliff on 2022/03/24.
 *
 * Description: 캐릭터 수정 커스텀 뷰
 */
class CharacterEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var colorIndex = 0
    private var type = 0
    private var pattern = 0
    private var patternColor = 0
    private var bodyColor = 0
    private var listener: CharacterChangeListener? = null
    private var characterView: CharacterView? = null

    /**
     * 캐릭터 초기화
     */
    fun initialize(
        colorIndex: Int,
        type: Int,
        pattern: Int,
        patternColorIndex: Int,
        bodyColorIndex: Int,
        listener: CharacterChangeListener
    ) {
        this.colorIndex = colorIndex
        this.type = type
        this.pattern = pattern
        this.patternColor = patternColorIndex
        this.bodyColor = bodyColorIndex
        this.listener = listener

        LayoutInflater.from(context).inflate(R.layout.layout_character_edit_view, this, true)

        characterView = findViewById(R.id.character_view)

        val tabViewGroup = (findViewById<TabLayout>(R.id.tabLayout).get(0) as ViewGroup)[0] as ViewGroup
        tabViewGroup.let {
            it.children.forEach { childView ->
                if(childView is TextView) {
                    val typeface = Typeface.createFromAsset(resources.assets, "nanum_square_round_b.ttf")
                    childView.typeface = typeface
                }
            }
        }

        /**
         * 탭 선택 시 폰트 변경, View 변경
         */
        findViewById<TabLayout>(R.id.tabLayout).addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabViewGroup = (findViewById<TabLayout>(R.id.tabLayout).get(0) as ViewGroup)[tab?.position?: 0] as ViewGroup
                tabViewGroup.let {
                    it.children.forEach { childView ->
                        if(childView is TextView) {
                            val typeface = Typeface.createFromAsset(resources.assets, "nanum_square_round.ttf")
                            childView.typeface = typeface
                        }
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabViewGroup = (findViewById<TabLayout>(R.id.tabLayout).get(0) as ViewGroup)[tab?.position?: 0] as ViewGroup
                tabViewGroup.let {
                    it.children.forEach { childView ->
                        if(childView is TextView) {
                            val typeface = Typeface.createFromAsset(resources.assets, "nanum_square_round_b.ttf")
                            childView.typeface = typeface
                        }
                    }
                }
                when (tab.position) {
                    0 -> {
                        val container = findViewById<ViewGroup>(R.id.container)
                        container.removeAllViews()
                        container.addView(
                            TypeEditView(
                                context,
                                this@CharacterEditView.colorIndex,
                                this@CharacterEditView.type,
                                this@CharacterEditView.bodyColor,
                                this@CharacterEditView.pattern,
                                this@CharacterEditView.patternColor,
                                object : TypeEditView.ChangeTypeListener {
                                    override fun onTypeChanged(type: Int) {
                                        updateType(type)
                                    }

                                    override fun onColorChanged(colorIndex: Int) {
                                        updateColor(colorIndex)
                                    }

                                })
                        )
                    }
                    1 -> {
                        val container = findViewById<ViewGroup>(R.id.container)
                        container.removeAllViews()
                        container.addView(
                            PatternEditView(
                                context,
                                this@CharacterEditView.patternColor,
                                this@CharacterEditView.pattern,
                                this@CharacterEditView.colorIndex,
                                this@CharacterEditView.bodyColor,
                                this@CharacterEditView.type,
                                object : PatternEditView.ChangePatternListener {
                                    override fun onPatternChanged(type: Int) {
                                        updatePattern(type)
                                    }

                                    override fun onColorChanged(colorIndex: Int) {
                                        updatePatternColor(colorIndex)
                                    }
                                })
                        )

                    }
                    2 -> {
                        val container = findViewById<ViewGroup>(R.id.container)
                        container.removeAllViews()
                        container.addView(
                            BodyEditView(
                                context,
                                this@CharacterEditView.bodyColor,
                                object : BodyEditView.ChangeBodyListener {
                                    override fun onColorChanged(colorIndex: Int) {
                                        updateBodyColor(colorIndex)
                                    }
                                })
                        )
                    }
                }
            }
        })

        updateColor(this.colorIndex)
        updateType(this.type)
        updatePatternColor(this.patternColor)
        updatePattern(this.pattern)
        updateBodyColor(this.bodyColor)

        val container = findViewById<ViewGroup>(R.id.container)
        container.removeAllViews()
        container.addView(
            TypeEditView(
                context,
                this@CharacterEditView.colorIndex,
                this@CharacterEditView.type,
                this@CharacterEditView.bodyColor,
                this@CharacterEditView.pattern,
                this@CharacterEditView.patternColor,
                object : TypeEditView.ChangeTypeListener {
                    override fun onTypeChanged(type: Int) {
                        updateType(type)
                    }

                    override fun onColorChanged(colorIndex: Int) {
                        updateColor(colorIndex)
                    }

                })
        )
    }

    /**
     * 타입 업데이트
     */
    private fun updateType(index: Int) {
        type = index
        characterView?.updateType(type)
        listener?.onTypeChanged(index)
    }

    /**
     * 색상 업데이트
     */
    private fun updateColor(index: Int) {
        colorIndex = index
        characterView?.updateColor(colorIndex)
        listener?.onColorChanged(index)
    }

    /**
     * 패턴 업데이트
     */
    private fun updatePattern(index: Int) {
        pattern = index
        characterView?.updatePattern(pattern)
        listener?.onPatternChanged(index)
    }

    /**
     * 패턴 색상 업데이트
     */
    private fun updatePatternColor(index: Int) {
        patternColor = index
        characterView?.updatePatternColor(patternColor)
        listener?.onPatternColorChanged(index)
    }

    /**
     * 바디 색상 업데이트
     */
    private fun updateBodyColor(index: Int) {
        bodyColor = index
        characterView?.updateBodyColor(bodyColor)
        listener?.onBodyColorChanged(index)
    }

    interface CharacterChangeListener {
        fun onColorChanged(index: Int)
        fun onTypeChanged(index: Int)
        fun onPatternChanged(index: Int)
        fun onPatternColorChanged(index: Int)
        fun onBodyColorChanged(index: Int)
    }
}