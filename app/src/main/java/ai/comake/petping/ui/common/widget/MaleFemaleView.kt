package ai.comake.petping.ui.common.widget

import ai.comake.petping.R
import ai.comake.petping.util.setSafeOnClickListener
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * android-petping-2
 * Class: MaleFemaleView
 * Created by cliff on 2022/03/29.
 *
 * Description: 성별 선택 커스텀 뷰
 */
class MaleFemaleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var listener: GenderChangeListener? = null
    private var gender = -1
    private fun initView() {
        findViewById<View>(R.id.boy_btn).setSafeOnClickListener(this)
        findViewById<View>(R.id.girl_btn).setSafeOnClickListener(this)
    }

    fun initialize(gender: Int, listener: GenderChangeListener) {
        LayoutInflater.from(context).inflate(R.layout.layout_male_female_view, this, true)
        initView()
        setGender(gender)
        this.listener = listener
    }

    override fun onClick(view: View) {
        if(!isEnabled) return
        hideKeyboard()
        when (view.id) {
            R.id.boy_btn -> {
                setGender(1)
            }
            R.id.girl_btn -> {
                setGender(2)
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun setDisable(disable: Boolean) {
        val boy = findViewById<RelativeLayout>(R.id.boy_btn)
        val girl = findViewById<RelativeLayout>(R.id.girl_btn)
        boy.isEnabled = !disable
        girl.isEnabled = !disable
        isEnabled = !disable
    }

    fun setGender(gender: Int) {
        if(this.gender == gender) return
        this.gender = gender
        when (gender) {
            1 -> {
                findViewById<View>(R.id.boy_btn).isSelected = true
                findViewById<ImageView>(R.id.boy_image).setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_pink
                    )
                )
                findViewById<TextView>(R.id.boy_text).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_pink
                    )
                )

                findViewById<View>(R.id.girl_btn).isSelected = false
                if (isEnabled) {
                    findViewById<ImageView>(R.id.girl_image).setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_9_111
                        )
                    )
                    findViewById<TextView>(R.id.girl_text).setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_9_111
                        )
                    )
                } else {
                    findViewById<ImageView>(R.id.girl_image).setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_4_ddd
                        )
                    )
                    findViewById<TextView>(R.id.girl_text).setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_g_5_bbb
                        )
                    )

                    findViewById<ImageView>(R.id.girl_image).setBackgroundResource(R.drawable.ic_female_disable)
                    findViewById<View>(R.id.girl_btn).setBackgroundResource(R.drawable.grey_round_rect)
                }
            }
            2 -> {
                findViewById<View>(R.id.girl_btn).isSelected = true
                findViewById<ImageView>(R.id.girl_image).setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_pink
                    )
                )
                findViewById<TextView>(R.id.girl_text).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_pink
                    )
                )

                findViewById<View>(R.id.boy_btn).isSelected = false
                if (isEnabled) {
                    findViewById<ImageView>(R.id.boy_image).setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_9_111
                        )
                    )
                    findViewById<TextView>(R.id.boy_text).setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_9_111
                        )
                    )
                } else {
                    findViewById<ImageView>(R.id.boy_image).setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_4_ddd
                        )
                    )
                    findViewById<TextView>(R.id.boy_text).setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.greyscale_g_5_bbb
                        )
                    )

                    findViewById<ImageView>(R.id.boy_image).setBackgroundResource(R.drawable.ic_male_disable)
                    findViewById<View>(R.id.boy_btn).setBackgroundResource(R.drawable.grey_round_rect)
                }
            }
            else -> {
                findViewById<View>(R.id.girl_btn).isSelected = false
                findViewById<ImageView>(R.id.girl_image).setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.greyscale_9_111
                    )
                )
                findViewById<TextView>(R.id.girl_text).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.greyscale_9_111
                    )
                )

                findViewById<View>(R.id.boy_btn).isSelected = false
                findViewById<ImageView>(R.id.boy_image).setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.greyscale_9_111
                    )
                )
                findViewById<TextView>(R.id.boy_text).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.greyscale_9_111
                    )
                )
            }
        }

        listener?.onGenderChanged(gender)
    }

    interface GenderChangeListener {
        fun onGenderChanged(gender: Int)
    }
}