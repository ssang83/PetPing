package ai.comake.petping.ui.common.dialog

import ai.comake.petping.R
import ai.comake.petping.util.setSafeOnClickListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat

/**
 * android-petping-2
 * Class: PermissionDialog
 * Created by cliff on 2022/07/27.
 *
 * Description: 앱 최초 실행 시 권한 팝업
 */
class PermissionDialog(
    context: Context,
    private val cancelable: Boolean = false,
    private val btnCallback: (() -> Unit)? = null
) : Dialog(context, R.style.CustomDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_permission)
        setCancelable(cancelable)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = ContextCompat.getColor(context, R.color.greyscale_g_dimd)

        findViewById<Button>(R.id.btn_ok).setSafeOnClickListener {
            dismiss()
            btnCallback?.let { it() }
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
        }
    }
}