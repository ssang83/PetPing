package ai.comake.petping.util

import ai.comake.petping.R
import ai.comake.petping.ui.base.BaseViewHolder
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Outline
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern

/**
 * android-petping-2
 * Class: Extension
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */

const val EMAIL_PATTERN =
    "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&+=~()<>{}-])(?=\\S+\$).{8,}\$"
const val ID_PATTERN = "^(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1])[1-8])\$"
const val ID_FULL_PATTERN =
    "^(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1])[1-8][0-9]{6})\$"
const val BIRTH_PATTERN = "^(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))\$"
const val HANGUEL_PATTERN = "^(?=.*[가-힣])(?=\\S+\$).{1,}\$"
const val NUMBER_PATTERN = "^[0-9]+$"

//const val HANGUEL_PATTERN_NEW = "^[가-힣ㄱ-ㅎㅏ-ㅣ\\s]+$"
const val HANGUEL_PATTERN_NEW = "^[가-힣]*$" // 한글 자음/모음 및 공백 제거
const val HANGUEL_PATTERN_ADD_SPACE = "^[가-힣\\s]*$" // 한글 자음/모음 제거 및 공백 추가
const val NUMBER_DOT_PATTERN = "^[0-9.]+$"
const val CERTIFICATION_NAME_PATTERN = "^[가-힣a-zA-Z\\s]+$"
const val HANGUEL_PATTERN_NEW_FIX = "^[가-힣\\u318D\\u119E\\u11A2\\u2022\\u2025\\u00B7\\uFE55]+$" // 천지인

inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    block: Intent.() -> Unit
) {
    val intent = Intent(this, T::class.java)
    intent.block()
    startActivityForResult(intent, requestCode)
}

fun Context.isInternetConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networks = cm.allNetworks
    var hasInternet = false
    if (networks.isNotEmpty()) {
        networks.forEach {
            val networkCapabilities = cm.getNetworkCapabilities(it)
            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                hasInternet = true
            }
        }
    }
    return hasInternet
}

fun Int.toPriceFormat(): String {
    val df = DecimalFormat("#,###")
    return df.format(this.toLong())
}

fun Number.toPingFormat(): String {
    val df = DecimalFormat("###,###")
    return df.format(this.toLong())
}

fun String.toNumberFormat(): String {
    return String.format("%,d", this.toInt())
}

private val onClickCheck = AtomicBoolean(true)
private val timeChecker = AtomicLong(0L)
private const val btnDelay = 500L

class SafeClickListener(
    private val onSafeClick: (View) -> Unit
) : View.OnClickListener {
    override fun onClick(view: View) {
        if (onClickCheck.getAndSet(false)) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - timeChecker.get() >= btnDelay) {
                timeChecker.set(currentTime)
                onSafeClick(view)
            }
            onClickCheck.set(true)
        }
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    setOnClickListener(
        SafeClickListener { view ->
            onSafeClick(view)
        }
    )
}

fun View.setSafeOnClickListener(onSafeClick: View.OnClickListener) {
    setOnClickListener(
        SafeClickListener { view ->
            onSafeClick.onClick(view)
        }
    )
}

fun TextView.setCompletionState(stateStr: String) {
    this.text = stateStr
    when (stateStr) {
        "기간 종료" -> {
            this.setTextColor(ContextCompat.getColor(context, R.color.greyscale_9_aaa))
        }
        "인증 대기" -> {
            this.setTextColor(ContextCompat.getColor(context, R.color.greyscale_9_777))
        }
        "달성" -> {
            this.setTextColor(ContextCompat.getColor(context, R.color.primary_pink))
        }
    }
}

fun TextView.setOngoingState(stateStr: String, btn: View) {
    when (stateStr) {
        "" -> {
            btn.visibility = View.VISIBLE
            this.visibility = View.GONE
        }
        else -> {
            btn.visibility = View.GONE
            this.visibility = View.VISIBLE
            this.text = stateStr
            this.setTextColor(ContextCompat.getColor(context, R.color.greyscale_9_777))
        }
    }
}

fun recyclerViewSetup(
    context: Context,
    recyclerView: RecyclerView,
    adapter: RecyclerView.Adapter<BaseViewHolder>,
    @RecyclerView.Orientation direction: Int = RecyclerView.VERTICAL
) {
    val layoutManager = LinearLayoutManager(context)
    layoutManager.orientation = direction
    recyclerView.layoutManager = layoutManager
    recyclerView.itemAnimator = DefaultItemAnimator()
    recyclerView.adapter = adapter
}

fun Context.copyBitmapToFile(bitmap: Bitmap, filename: String): Uri? {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir = File("${externalCacheDir}/Petping")

    if (!storageDir.exists())
        storageDir.mkdir()

    var file: File? = null
    try {
        file = File.createTempFile(
            "PETPING_${timeStamp}",
            ".png",
            storageDir
        )
    } catch (exception: Exception) {
        return null
    } finally {
        file?.deleteOnExit()
    }


    var outputStream: FileOutputStream? = null
    try {
        outputStream = file?.outputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream?.close()
    } catch (exception: Exception) {
        return null
    } finally {
        outputStream?.close()
    }
    return file?.toUri()
}

fun Int.toGender(): String {
    return if (this == 1) "남"
    else "여"
}

/**
 * 만 나이 계산
 */
fun String.toAge(): String {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val birthYear =
        if (this.substring(0, 2).toInt() > currentYear % 100) "19${this.substring(0, 2)}"
        else "20${this.substring(0, 2)}"
    val birthMonth = this.substring(2, 4)
    val birthDay = this.substring(4, 6)
    var age = currentYear - birthYear.toInt()
    val beginDay = Calendar.getInstance().apply {
        set(Calendar.YEAR, birthYear.toInt())
        set(Calendar.MONTH, birthMonth.toInt())
        set(Calendar.DAY_OF_MONTH, birthDay.toInt())
    }.timeInMillis
    val lastDay = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, currentDay)
    }.timeInMillis
    val fewDay = (lastDay - beginDay) / (24 * 60 * 60 * 1000)
    val startDate = LocalDate.of(
        Integer.parseInt(birthYear),
        Integer.parseInt(birthMonth),
        Integer.parseInt(birthDay)
    )
    val period: Period = startDate.until(LocalDate.now())

    if (birthMonth.toInt() * 100 + birthDay.toInt() > currentMonth * 100 + currentDay) age--
    if (age > 0) return "${age}세"
    else return "${period.months}개월"
}

fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> Fragment.getNavigationResultLiveData(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun Activity.backStack(@IdRes viewId: Int) {
    findNavController(viewId).popBackStack()
}

fun Activity.multipleBackStack(@IdRes viewId: Int, @IdRes destinationId: Int) {
    findNavController(viewId).popBackStack(destinationId, inclusive = false)
}

fun Activity.navigateUp(@IdRes viewId: Int) {
    findNavController(viewId).navigateUp()
}

/**
 * TextInputLayout의 hint color를 변경해주는 함수
 */
fun setTextInputLayoutHintColor(
    textInputLayout: TextInputLayout,
    context: Context,
    @ColorRes colorIdRes: Int
) {
    textInputLayout.defaultHintTextColor =
        ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
}

/**
 * 기본 TextInputLayout 인터렉션 가이드
 */
fun setEditText(
    context: Context,
    layout: TextInputLayout,
    editText: EditText,
    pattern: Pattern,
    error: String,
    hint: String,
    focusHint: String,
    helper: String = ""
) {
    editText.setOnFocusChangeListener { _, b ->
        var hintInfo = focusHint
        if (!b) {
            layout.endIconMode = TextInputLayout.END_ICON_NONE
            val str = editText.text
            if (str.isEmpty()) {
                layout.isErrorEnabled = false
                hintInfo = hint
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_4_ddd)
            } else {
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            }
            if (!layout.isErrorEnabled) layout.helperText = ""
        } else {
            layout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
            setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            if (!layout.isErrorEnabled) layout.helperText = helper
        }

        layout.hint = hintInfo
    }

    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            str?.let {
                if (str.isEmpty()) {
                    layout.isErrorEnabled = false
                } else if (focusHint == "생년월일") {
                    if (str.length < 6) {
                        if (!pattern.matcher(str).matches()) {
                            layout.isErrorEnabled = true
                            layout.error = "숫자만 입력할 수 있습니다."
                        } else {
                            layout.isErrorEnabled = true
                            layout.error = error
                        }
                    } else {
                        layout.isErrorEnabled = false
                    }
                } else if (focusHint == "종류") {
                    if (str.toString().replace(" ", "") == "") {
                        layout.isErrorEnabled = true
                        layout.error = error
                    } else {
                        if (!pattern.matcher(str).matches()) {
                            if (!layout.isErrorEnabled) {
                                layout.isErrorEnabled = true
                                layout.error = error
                            }
                        } else {
                            layout.isErrorEnabled = false
                        }
                    }
                } else {
                    if (!pattern.matcher(str).matches()) {
                        if (!layout.isErrorEnabled) {
                            layout.isErrorEnabled = true
                            layout.error = error
                        }
                    } else {
                        layout.isErrorEnabled = false
                    }
                }
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    })
}

/**
 * hint만 있는 경우
 */
fun setEditText(
    context: Context,
    layout: TextInputLayout,
    editText: EditText,
    hint: String, focusHint: String
) {
    editText.setOnFocusChangeListener { _, b ->
        var hintInfo = focusHint
        if (!b) {
            layout.endIconMode = TextInputLayout.END_ICON_NONE
            if (editText.text.isEmpty()) {
                hintInfo = hint
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_4_ddd)
            } else {
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            }
        } else {
            layout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
            setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
        }
        layout.hint = hintInfo
    }
}

/**
 * 비밀번호 확인
 */
fun setEditText(
    context: Context,
    layout: TextInputLayout,
    editText: EditText,
    origin: EditText,
    error: String,
    hint: String,
    focusHint: String,
    helper: String = "",
    needInitialized: Boolean = false,
    initializeText: String = ""
) {
    layout.helperText = helper

    editText.setOnFocusChangeListener { _, b ->
        var hintInfo = focusHint

        if (!b) {
            layout.endIconMode = TextInputLayout.END_ICON_NONE
            val str = editText.text
            if (str.isEmpty()) {
                layout.isErrorEnabled = false
                hintInfo = hint
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_4_ddd)
            } else {
                if (origin.text.toString() != str.toString()) {
                    layout.isErrorEnabled = true
                    layout.error = error
                } else {
                    layout.isErrorEnabled = false
                }
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            }
        } else {
            layout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
            setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            if (needInitialized && editText.text.toString() == initializeText && !layout.isErrorEnabled) {
                editText.text.clear()
            }
        }

        layout.hint = hintInfo
    }

    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            str?.let {
                if (str.isEmpty()) {
                    layout.isErrorEnabled = false
                } else {
                    if (origin.text.toString() != str.toString()) {
                        if (!layout.isErrorEnabled) {
                            layout.isErrorEnabled = true
                            layout.error = error
                        }
                    } else {
                        layout.isErrorEnabled = false
                    }
                }
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    })
}

/**
 * initializeText(********)가 있을 때
 */
fun setEditText(
    context: Context,
    layout: TextInputLayout,
    editText: EditText,
    pattern: Pattern,
    error: String,
    hint: String,
    focusHint: String,
    helper: String = "",
    needInitialized: Boolean = false,
    initializeText: String = ""
) {
    editText.setOnFocusChangeListener { _, b ->
        var hintInfo = focusHint

        if (!b) {
            layout.endIconMode = TextInputLayout.END_ICON_NONE
            val str = editText.text
            if (str.isEmpty()) {
                layout.isErrorEnabled = false
                hintInfo = hint
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_4_ddd)
            } else {
                if (!pattern.matcher(str).matches()) {
                    layout.isErrorEnabled = true
                    layout.error = error
                } else {
                    layout.isErrorEnabled = false
                }
                setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            }
        } else {
            layout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
            setTextInputLayoutHintColor(layout, context, R.color.greyscale_9_aaa)
            if (needInitialized && editText.text.toString() == initializeText && !layout.isErrorEnabled) {
                editText.text.clear()
            }
            layout.helperText = helper
        }

        layout.hint = hintInfo
    }

    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            str?.let {
                if (str.isEmpty()) {
                    layout.isErrorEnabled = false
                } else {
                    if (pattern.matcher(str).matches()) {
                        layout.isErrorEnabled = false
                    }
                }
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    })
}

fun roundTop(iv: ImageView, curveRadius : Float)  : ImageView {
    iv.outlineProvider = object : ViewOutlineProvider() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, (view.height+curveRadius).toInt(), curveRadius)
        }
    }

    iv.clipToOutline = true
    return iv
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}