package ai.comake.petping.ui.etc.setting.policy

import ai.comake.petping.R
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentPolicyBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

/**
 * android-petping-2
 * Class: PolicyFragment
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
class PolicyFragment : BaseFragment<FragmentPolicyBinding>(FragmentPolicyBinding::inflate) {

    private val viewModel: PolicyViewModel by viewModels()
    private val args:PolicyFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            moveToServicePolicy.observeEvent(viewLifecycleOwner) {
                val config = WebConfig(
                    url = args.info.policyServiceUrl
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(PolicyFragmentDirections.actionPolicyFragmentToContentsWebFragment(config))
            }

            moveToLocationPolicy.observeEvent(viewLifecycleOwner) {
                val config = WebConfig(
                    url = args.info.policyLocationServiceUrl
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(PolicyFragmentDirections.actionPolicyFragmentToContentsWebFragment(config))
            }

            moveToPrivacyPolicy.observeEvent(viewLifecycleOwner) {
                val config = WebConfig(
                    url = args.info.personalInfoCollectionUrl
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(PolicyFragmentDirections.actionPolicyFragmentToContentsWebFragment(config))
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}