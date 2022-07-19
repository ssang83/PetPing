package ai.comake.petping.ui.common.dialog

import ai.comake.petping.R
import ai.comake.petping.databinding.PopupDialogSingleBtnBinding
import ai.comake.petping.util.setSafeOnClickListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat

class SingleBtnDialog(
    context: Context,
    private val title: String,
    private val description: String,
    private val cancelable: Boolean = false,
    private val btnCallback: (() -> Unit)? = null
) : Dialog(context, R.style.CustomDialog) {
    private lateinit var binding: PopupDialogSingleBtnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PopupDialogSingleBtnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.isEmpty = title.isEmpty()
        binding.title.text = title
        binding.description.text = description

        setCancelable(cancelable)
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = ContextCompat.getColor(context, R.color.greyscale_g_dimd)

        binding.btnOk.setSafeOnClickListener {
            dismiss()
            btnCallback?.let { it() }
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = ContextCompat.getColor(context, android.R.color.transparent)
        }
    }
}