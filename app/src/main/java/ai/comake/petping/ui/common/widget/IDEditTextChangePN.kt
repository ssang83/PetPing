package ai.comake.petping.ui.common.widget

import ai.comake.petping.R
import ai.comake.petping.util.ID_PATTERN
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.Exception
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: IDEditTextChangePN
 * Created by cliff on 2022/03/17.
 *
 * Description: 휴대폰 번호 수정에서 생년월일과 성별 입력 필드
 */
class IDEditTextChangePN @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_id_input_pn, this, true)
        initView()
    }

    private fun initView() {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        val wrapper = findViewById<TextInputLayout>(R.id.id_wrapper)

        firstTv.setOnFocusChangeListener { _, b ->
            var hintInfo = "주민등록번호 / 외국인등록번호"
            if(!b && firstTv.text.isEmpty()) {
                hintInfo = "YYMMDD"
            }

            wrapper.hint = hintInfo
        }

        firstTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length >= 6) {
                    editable.delete(6, editable.length)
                    requestFocus(FOCUS_BACKWARD)
                }
            }
        })


        secondTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty() && !editable.contains("******")) {
                    editable.delete(1, editable.length)
                    secondTv.setText("$editable******")
                    rootView.findViewById<View>(nextFocusDownId).requestFocus()
                } else if (editable.length == 6 && editable.contains("******")) {
                    secondTv.setText("")
                }
            }
        })

        secondTv.setOnFocusChangeListener { _, b ->
            if (b) {
                secondTv.text.clear()
            }
        }
    }

    fun isValid(): Boolean {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        return Pattern.compile(ID_PATTERN).matcher(getIdInfo()).matches()
    }

    fun getIdInfo(): String {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        var ret = ""

        ret += firstTv.text
        if (secondTv.text.isNotEmpty()) ret += secondTv.text[0]

        return ret
    }

    fun disableAll(input: String) {
        try {
            val firstTv = findViewById<EditText>(R.id.id_first_tv)
            val secondTv = findViewById<EditText>(R.id.id_second_tv)
            firstTv.isEnabled = false
            secondTv.isEnabled = false

            firstTv.setText(input.substring(0, 6))
            secondTv.setText("${input.substring(6)}******")
        } catch (e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun disableAll() {
        val firstTv = findViewById<EditText>(R.id.id_first_tv)
        val secondTv = findViewById<EditText>(R.id.id_second_tv)
        firstTv.isEnabled = false
        secondTv.isEnabled = false
    }
}