package ai.comake.petping.ui.home.insurance

import ai.comake.petping.AppConstants
import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentInsuranceBinding
import ai.comake.petping.databinding.FragmentRewardBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateBlackStatusBar
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: InsuranceFragment
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@AndroidEntryPoint
class InsuranceFragment : Fragment() {
    private lateinit var binding: FragmentInsuranceBinding
    private val viewModel by viewModels<InsuranceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsuranceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log("TAG","")
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            loadData()

            moveToInsurance.observeEvent(viewLifecycleOwner) {
                //TODO : 펫보험 바로가기
            }
        }

        binding.noLoginLayer.login.setSafeOnClickListener {
            requireActivity().findNavController(R.id.nav_main)
                .navigate(R.id.action_homeScreen_to_loginGraph)
        }

        binding.noProfileLayer.makeProfile.setSafeOnClickListener {
            Toast.makeText(requireContext(), "반려견 프로필 만들기로 이동", Toast.LENGTH_SHORT).show()
        }

        updateStatusBar()
    }

    private fun updateStatusBar() {
        if (AppConstants.PROFILE_HEADER_IS_VISIBLE || AppConstants.LOGIN_HEADER_IS_VISIBLE) {
            updateBlackStatusBar(requireActivity().window)
        } else {
            updateWhiteStatusBar(requireActivity().window)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtil.log("TAG", "show ${!hidden}")
    }

    companion object {
        const val TAG = "InsuranceFragment"
    }
}