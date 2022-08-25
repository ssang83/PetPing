package ai.comake.petping.ui.profile.make

import ai.comake.petping.*
import ai.comake.petping.AppConstants.DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT
import ai.comake.petping.databinding.FragmentProfileBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes

/**
 * android-petping-2
 * Class: ProfileFragment
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()
    private val args: ProfileGraphArgs by navArgs()

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (args.needBack.not()) {
                finishApplication()
            } else {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        LogUtil.log("needBack : ${args.needBack}")

        with(viewModel) {

            needBack.value = args.needBack

            moveToMakeProfile.observeEvent(viewLifecycleOwner) {
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "airbridge.petprofile.make",
                    "petprofile_button_profilemake",
                    "petprofile_button_profilemake_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_profileFragment_to_newProfileFirstFragment)
            }

            moveToConnectFamily.observeEvent(viewLifecycleOwner) {
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "airbridge.petprofile.make",
                    "petprofile_button_familypetsignin",
                    "petprofile_button_familypetsignin_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(R.id.action_profileFragment_to_familyFragment)
            }
        }

        binding.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    /**
     * '뒤로가기' 버튼 2회 연속 입력을 통한 종료를 사용자에게 안내하고 처리
     */
    private var backPressedTime: Long = 0
    private fun finishApplication() {
        if (System.currentTimeMillis() - backPressedTime < DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT) {
            requireActivity().finish()
            return
        }
        Toast.makeText(activity, getString(R.string.finish_app_guide), Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }
}