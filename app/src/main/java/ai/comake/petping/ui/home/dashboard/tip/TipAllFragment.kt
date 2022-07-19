package ai.comake.petping.ui.home.dashboard.tip

import ai.comake.petping.R
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentTipAllBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: TipAllFragment
 * Created by cliff on 2022/05/20.
 *
 * Description:
 */
@AndroidEntryPoint
class TipAllFragment : BaseFragment<FragmentTipAllBinding>(FragmentTipAllBinding::inflate) {

    private val viewModel: TipAllViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWhiteStatusBar(requireActivity().window)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData()

            moveToPingTip.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    TipAllFragmentDirections.actionTipAllFragmentToContentsWebFragment(config)
                )
            }
        }

        binding.btnBack.setSafeOnClickListener {
            requireActivity().backStack(R.id.nav_main)
        }
    }
}
