package ai.comake.petping.ui.home.dashboard

import ai.comake.petping.R
import ai.comake.petping.data.vo.Pet
import ai.comake.petping.data.vo.WalkHistory
import ai.comake.petping.databinding.DialogPetProfileBinding
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.setSafeOnClickListener
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * android-petping-2
 * Class: PetSelectBottomDialogFragment
 * Created by cliff on 2022/05/20.
 *
 * Description:
 */
class PetSelectBottomDialogFragment(
    private var petList: List<Pet>,
    private val petId: Int,
    private val viewModel: DashboardViewModel
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogPetProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentPet = petList.find { it.id.toInt() == petId }

        val adapterPets = ArrayList<Pet>()
        petList.forEach {
            if(it.id.toInt() != petId) adapterPets.add(it)
        }

        binding.petList.apply {
            itemAnimator = DefaultItemAnimator()
            adapter = PetAdapter(requireContext(), viewModel, adapterPets) {
                dismiss()
            }
        }

        // 158 : 펫프로필영역 85: item의 각각 Height 45 상단 고정영역
        val layoutParams = binding.petList.layoutParams
        val calculationHeight = (158 * resources.displayMetrics.density).toInt() + ((petList.size + 1) * 86 * resources.displayMetrics.density).toInt()

        if(calculationHeight > (resources.displayMetrics.heightPixels - (126 * resources.displayMetrics.density).toInt())) {
            layoutParams.height = (resources.displayMetrics.heightPixels - ((158 + 126) * resources.displayMetrics.density).toInt())
            binding.petList.layoutParams = layoutParams
            binding.petList.requestLayout()
        } else {
            layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            binding.petList.layoutParams = layoutParams
            binding.petList.requestLayout()
        }

        setCurrentPetProfile(currentPet!!)
    }

    private fun setCurrentPetProfile(currentPet:Pet) {
        with(binding) {

            Glide.with(requireContext())
                .load(currentPet.profileImageURL)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage)

            name.text = currentPet.name

            if (currentPet.breed.length <= 15) {
                description.text = "${currentPet.breed} . ${currentPet.age} . ${currentPet.gender}"
            } else {
                description.text = "${currentPet.breed.substring(0, 15)} . ${currentPet.age} . ${currentPet.gender}"
            }

            familyMark.visibility = if (currentPet.isFamilyProfile) {
                View.VISIBLE
            } else {
                View.GONE
            }

            profileBtn.visibility = if (currentPet.isFamilyProfile) {
                View.GONE
            } else {
                View.VISIBLE
            }

            profileBtn.setSafeOnClickListener {
                viewModel.goToPetProfile(petId)
                dismiss()
            }

            historyBtn.setSafeOnClickListener {
                val viewMode = if(currentPet.isFamilyProfile) "family" else "master"
                WalkHistory(petId, viewMode).let { viewModel.goToWalkHistory(it) }
                dismiss()
            }
        }
    }
}