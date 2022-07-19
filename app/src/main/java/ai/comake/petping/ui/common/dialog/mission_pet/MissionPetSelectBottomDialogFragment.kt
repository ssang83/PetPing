package ai.comake.petping.ui.common.dialog.mission_pet

import ai.comake.petping.R
import ai.comake.petping.data.vo.InsurancePet
import ai.comake.petping.databinding.BottomDialogPetSelectBinding
import ai.comake.petping.util.recyclerViewSetup
import ai.comake.petping.util.setSafeOnClickListener
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * android-petping-2
 * Class: MissionPetSelectBottomDialogFragment
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
class MissionPetSelectBottomDialogFragment(
    private val petList: List<InsurancePet>,
    private val btnCallback: ((petId: Int) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomDialogPetSelectBinding

    private lateinit var mAdapter:RepresentativePetAdapter
    private var petId = petList[0].petId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogPetSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = RepresentativePetAdapter(petList) {
            petId = it
            binding.walkBtn.isEnabled = true
        }

        recyclerViewSetup(requireContext(), binding.petList, mAdapter)

        binding.walkBtn.setSafeOnClickListener {
            btnCallback?.invoke(petId)
            dismiss()
        }

        binding.petList.apply {
            layoutParams = layoutParams

            val headerHeight = (139 * (resources.displayMetrics.density)).toInt()
            val buttonHeight = (56 * (resources.displayMetrics.density)).toInt()
            val calculationHeight =
                headerHeight + buttonHeight + (((petList.size) * 87) * resources.displayMetrics.density).toInt()

            if (calculationHeight > resources.displayMetrics.heightPixels - (126 * resources.displayMetrics.density).toInt()) {
                layoutParams.height =
                    (resources.displayMetrics.heightPixels - headerHeight - buttonHeight - (126 * resources.displayMetrics.density).toInt())
                layoutParams = layoutParams
                requestLayout()
            } else {
                layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
                layoutParams = layoutParams
                requestLayout()
            }
        }

        binding.walkBtn.isEnabled = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state =
                BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }
}