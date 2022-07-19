package ai.comake.petping.ui.home.walk.dialog

import ai.comake.petping.R
import ai.comake.petping.databinding.BottomSheetDialogWalkFinishBinding
import ai.comake.petping.util.setSafeOnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WalkEndBottomSheetDialog(val closePopup: () -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogWalkFinishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogWalkFinishBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setSafeOnClickListener {
            closePopup()
            dismissAllowingStateLoss()
        }
    }
}