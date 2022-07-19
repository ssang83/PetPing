package ai.comake.petping.ui.etc.setting

import ai.comake.petping.*
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentAppSettingBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: AppSettingFragment
 * Created by cliff on 2022/02/23.
 *
 * Description:
 */
@AndroidEntryPoint
class AppSettingFragment :
    BaseFragment<FragmentAppSettingBinding>(FragmentAppSettingBinding::inflate) {

    private val viewModel: AppSettingViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(
            "SCROLL_POSITION",
            intArrayOf(binding.scrollView.scrollX, binding.scrollView.scrollY)
        )
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { state ->
            val position = state.getIntArray("SCROLL_POSITION")
            if (position != null) binding.scrollView.post( {
                binding.scrollView.scrollTo(
                    position[0],
                    position[1]
                )
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToStore.observeEvent(viewLifecycleOwner) {
                requireContext().startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${requireContext().packageName}")
                })
            }

            moveToPolicy.observeEvent(viewLifecycleOwner) { appInfo ->
                requireActivity().findNavController(R.id.nav_main).navigate(
                    AppSettingFragmentDirections.actionAppSettingFragmentToPolicyFragment(appInfo)
                )
            }

            moveToLincense.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(AppSettingFragmentDirections.actionAppSettingFragmentToContentsWebFragment(config))
            }

            moveToBuisnessInfo.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(AppSettingFragmentDirections.actionAppSettingFragmentToContentsWebFragment(config))
            }
        }

        binding.header.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}