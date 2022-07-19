package ai.comake.petping.ui.profile

import ai.comake.petping.*
import ai.comake.petping.data.vo.PetProfileData
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentSettingFamilyBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.LogUtil
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: SettingFamilyFragment
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@AndroidEntryPoint
class SettingFamilyFragment(
    private val petId:Int
) : BaseFragment<FragmentSettingFamilyBinding>(FragmentSettingFamilyBinding::inflate) {

    private val viewModel: SettingFamilyViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            copyToClipboard.observeEvent(viewLifecycleOwner) { code ->
                copy(code)
            }

            unlinkFamilyPopup.observeEvent(viewLifecycleOwner) {
                DoubleBtnDialog(
                    requireContext(),
                    "등록을 해제하시겠어요?",
                    "가족에서 해제하시면 산책과 프로필을 공유할 수 없습니다.",
                    okCallback = {
                        unlinkFamilyCode()
                    }).show()
            }

            moveToFamilyInviteGuide.observeEvent(viewLifecycleOwner) {
                val config = WebConfig(
                    url = BuildConfig.FAMILY_CODE_GUIDE_URL
                )
                requireActivity().findNavController(R.id.nav_main).navigate(
                    SettingFragmentDirections.actionSettingFragmentToContentsWebFragment(config)
                )
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            viewModel.loadData(petId)
        }
    }

    private fun copy(code:String) {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("family code", code)
        clipboardManager.setPrimaryClip(clip)
        SingleBtnDialog(requireContext(), "가족코드를 복사했어요.", "가족에게 코드를 전달해 주세요.").show()
    }
}