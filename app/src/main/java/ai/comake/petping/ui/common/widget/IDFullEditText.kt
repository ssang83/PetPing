package ai.comake.petping.ui.common.widget

import ai.comake.petping.R
import ai.comake.petping.util.ID_FULL_PATTERN
import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: IDFullEditText
 * Created by cliff on 2022/03/23.
 *
 * Description: 펫보험 연결에서 입력 필드
 */
class IDFullEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_id_input_full, this, true)
        initView()
    }

    private fun initView() {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        firstTv.isEnabled = false
        secondTv.transformationMethod = SecondTvTransformationMethod()

        secondTv.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                secondTv.clearFocus()
                secondTv.hideKeyboard()
                handled = true
            }
            handled
        }

        secondTv.setOnFocusChangeListener { v, hasFocus ->
            findViewById<TextView>(R.id.id_guide_tv).apply {
                when (hasFocus) {
                    true -> visibility = View.VISIBLE
                    else -> visibility = View.GONE
                }
            }
        }
    }

    fun setBirth(birth: String) {
        findViewById<EditText>(R.id.id_first_tv).setText(birth)
    }

    fun isValid(): Boolean {
        return Pattern.compile(ID_FULL_PATTERN).matcher(getIdInfo()).matches()
    }

    fun getIdInfo(): String {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        var ret = ""

        ret += firstTv.text
        if (secondTv.text.isNotEmpty()) ret += secondTv.text

        return ret
    }

    fun disableAll() {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        firstTv.isEnabled = false
        secondTv.isEnabled = false
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    inner class SecondTvTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
            return PasswordCharSequence(source ?: "")
        }

        private inner class PasswordCharSequence(private val mSource: CharSequence) : CharSequence {
            override val length: Int
                get() = mSource.length

            override fun get(index: Int): Char {
                return if (index > 0) '*'
                else mSource[index]
            }

            override fun subSequence(start: Int, end: Int): CharSequence {
                return mSource.subSequence(start, end)
            }
        }
    }
}