package ai.comake.petping.ui.common.dialog

import ai.comake.petping.R
import ai.comake.petping.databinding.LoadingDialogBinding
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import androidx.databinding.DataBindingUtil

/**
 * android-petping-2
 * Class: LoadingDialog
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
class LoadingDialog(
    context: Context
) : AppCompatDialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding: LoadingDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(getContext()),
            R.layout.loading_dialog,
            null,
            false
        )
        setContentView(binding.root)

        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}