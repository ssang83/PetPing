package ai.comake.petping.ui.common.dialog

import ai.comake.petping.R
import ai.comake.petping.util.setSafeOnClickListener
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * android-petping-2
 * Class: DoubleBtnDialog
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
class DoubleBtnDialog(
    context: Context,
    private val title: String,
    private val description: String,
    private val cancelable: Boolean = false,
    private val cancelText: String = "아니요",
    private val okText: String = "네",
    private val cancelCallback: (() -> Unit)? = null,
    private val okCallback: (() -> Unit)? = null
) : Dialog(context, R.style.CustomDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_dialog_double_btn)
        setCancelable(cancelable)

        if (title.isNotEmpty()) {
            findViewById<TextView>(R.id.title).visibility = View.VISIBLE
            findViewById<TextView>(R.id.title).text = title
        } else {
            findViewById<TextView>(R.id.title).visibility = View.GONE
        }

        findViewById<TextView>(R.id.description).text = description
        findViewById<Button>(R.id.btn_cancel).text = cancelText
        findViewById<Button>(R.id.btn_ok).text = okText
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = ContextCompat.getColor(context, R.color.greyscale_g_dimd)
        }

        findViewById<Button>(R.id.btn_ok).setSafeOnClickListener {
            dismiss()
            okCallback?.let { it() }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window?.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
            }
        }

        findViewById<Button>(R.id.btn_cancel).setSafeOnClickListener {
            dismiss()
            cancelCallback?.let { it() }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window?.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
            }
        }
    }
}