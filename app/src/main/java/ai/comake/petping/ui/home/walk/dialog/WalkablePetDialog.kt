package ai.comake.petping.ui.home.walk.dialog

import ai.comake.petping.R
import ai.comake.petping.data.vo.WalkablePet
import ai.comake.petping.databinding.BottomSheetDialogWalkPetBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.home.walk.adapter.WalkablePetRecyclerViewAdapter
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.setSafeOnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalkablePetDialog(
    private var mPetList: ArrayList<WalkablePet.Pets>,
    private val callback: (List<WalkablePet.Pets>) -> Unit
) : BottomSheetDialogFragment() {
    private val walkablePetDialogViewModel by viewModels<WalkablePetDialogViewModel>()
    private lateinit var binding: BottomSheetDialogWalkPetBinding
    private lateinit var mListAdapter: WalkablePetRecyclerViewAdapter
    private var walkPets = listOf<WalkablePet.Pets>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogWalkPetBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpObserver()
        setOnClickListener()
    }

    private fun setUpAdapter() {
        if (isVisibleAllTogether(mPetList.size)) {
            mPetList.add(0, WalkablePet.Pets(false, "", R.drawable.ic_select_all, "모두 함께", -1))
        }

        mListAdapter = WalkablePetRecyclerViewAdapter(walkablePetDialogViewModel)
        binding.petListRecyclerView.adapter = mListAdapter
        mListAdapter.submitList(mPetList)
    }

    private fun setUpObserver() {
        walkablePetDialogViewModel.selectedPetIds.observeEvent(this) { item ->
            walkPets = item
            binding.startWalkButton.isEnabled = walkPets.isNotEmpty()
        }

        walkablePetDialogViewModel.isOverPetMaxSize.observeEvent(this) {
            activity?.let { it ->
                SingleBtnDialog(
                    it,
                    getString(R.string.is_over_pet_maxsize_title),
                    getString(R.string.is_over_pet_maxsize_desc)
                ) {

                }.show()
            }
        }
    }

    private fun setOnClickListener() {
        binding.startWalkButton.setSafeOnClickListener {
            callback.invoke(walkPets)
            dismissAllowingStateLoss()
        }
    }

    private fun isVisibleAllTogether(size: Int) = size in 1..5
}