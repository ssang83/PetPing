package ai.comake.petping.ui.etc.member_info.withdrawal

import ai.comake.petping.*
import ai.comake.petping.databinding.FragmentWithdrawalBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SelectBottomDialogFragment
import ai.comake.petping.util.KeyboardVisibilityUtils
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: WithdrawalFragment
 * Created by cliff on 2022/03/18.
 *
 * Description:
 */
@AndroidEntryPoint
class WithdrawalFragment :
    BaseFragment<FragmentWithdrawalBinding>(FragmentWithdrawalBinding::inflate) {

    private val viewModel: WithdrawalViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData()

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            selectReason.observeEvent(viewLifecycleOwner) {
                SelectBottomDialogFragment(
                    getString(R.string.withdrawal_reason_popup_title),
                    getString(R.string.confirm),
                    leaveType,
                    binding.inputLeaveSpinner.text.toString()
                ) { leaveString ->
                    if (leaveString.isNotEmpty()) {
                        binding.inputLeaveSpinner.setText(leaveString)
                        viewModel.reason.value = leaveString

                        if (leaveString.contains("직접 입력")) {
                            binding.otherReasonTextView.visibility = View.VISIBLE
                        } else {
                            binding.otherReasonTextView.visibility = View.GONE
                        }
                    }
                }.show(childFragmentManager, "")
            }

            moveToHome.observeEvent(viewLifecycleOwner) {
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_withdrawalFragment_to_homeScreen)
            }

            withdrawalPopup.observeEvent(viewLifecycleOwner) {
                DoubleBtnDialog(
                    requireContext(),
                    getString(R.string.withdrawal_popup_title),
                    getString(R.string.withdrawal_popup_desc),
                    cancelCallback = { requireActivity().backStack(R.id.nav_main) },
                    okCallback = { goToWithdrawal() }
                ).show()
            }
        }

        setUi()
    }

    override fun onDestroyView() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }

    private fun setUi() {
        with(binding) {
            otherReasonTextView.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    otherReasonTextView.background = requireActivity().getDrawable(R.drawable.btn_outline_black)
                    otherReasonTextView.hint = ""
                } else {
                    otherReasonTextView.background = requireActivity().getDrawable(R.drawable.btn_outline_ddd)
                    otherReasonTextView.hint = "사유를 입력해 주세요. (최대 1,000자)"
                }
            }

            otherReasonTextView.setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        true
                    }
                }
                false
            }

            keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
                onHideKeyboard = {
                    outSide.clearFocus()
                })

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}