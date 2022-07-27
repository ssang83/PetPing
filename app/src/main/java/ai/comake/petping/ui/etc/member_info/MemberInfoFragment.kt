package ai.comake.petping.ui.etc.member_info

import ai.comake.petping.*
import ai.comake.petping.data.vo.CIConfig
import ai.comake.petping.data.vo.ChangePhoneNumberConfig
import ai.comake.petping.databinding.FragmentMemberInfoBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.Event
import co.ab180.airbridge.event.StandardEventCategory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * android-petping-2
 * Class: MemberInfoFragment
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@AndroidEntryPoint
class MemberInfoFragment : BaseFragment<FragmentMemberInfoBinding>(FragmentMemberInfoBinding::inflate) {
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val viewModel: MemberInfoViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        getNavigationResultLiveData<String>("key")?.observe(viewLifecycleOwner) {
            LogUtil.log("TAG", "it $it") // 이메일 등록 및 수정할 경우 정보 갱신
            if("refresh_member_info" == it) {
                viewModel.loadData()
            }
        }

        getNavigationResultLiveData<CIConfig>("key")?.observe(viewLifecycleOwner) {
            // 휴대폰 번호 등록하는 경우 멤버 정보 갱신
            viewModel.loadData()
        }

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            emailAuthSuccessPopup.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.send_auth_mail_title),
                    getString(R.string.send_auth_mail_desc)
                ) {}.show()
            }

            moveToWithDrawal.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_memberInfoFragment_to_withdrawalFragment)
            }

            moveToChangePw.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_memberInfoFragment_to_changePasswordFragment)
            }

            moveToChangePhoneNumber.observeEvent(viewLifecycleOwner) {
                val config = ChangePhoneNumberConfig(
                    name = name.value.toString(),
                    birthAndGender = birthAndGender.value.toString()
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    MemberInfoFragmentDirections.actionMemberInfoFragmentToChangePhoneNumberFragment(
                        config
                    )
                )
            }

            moveToAddPhoneNumber.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_memberInfoFragment_to_certWebFragment)
            }

            moveToChangeEmail.observeEvent(viewLifecycleOwner) {
                bundleOf("email" to email.value.toString()).let {
                    requireActivity().findNavController(R.id.nav_main).navigate(R.id.action_memberInfoFragment_to_changeEmailFragment, it)
                }
            }

            moveToRegisterEmail.observeEvent(viewLifecycleOwner) {
                bundleOf("email" to "").let {
                    requireActivity().findNavController(R.id.nav_main).navigate(R.id.action_memberInfoFragment_to_changeEmailFragment, it)
                }
            }

            moveToLocationHistory.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_memberInfoFragment_to_locationHistoryFragment)
            }

            logout.observe(viewLifecycleOwner) {
                // airbridge logout event
                val event = Event(StandardEventCategory.SIGN_OUT)
                Airbridge.trackEvent(event)
                Airbridge.expireUser()

                AppConstants.AUTH_KEY = ""
                AppConstants.ID = ""

                sharedPreferencesManager.deleteLoginDataStore()

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_memberInfoFragment_to_loginScreen)
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}