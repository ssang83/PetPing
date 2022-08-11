package ai.comake.petping.ui.home.insurance

import ai.comake.petping.R
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentInsuranceBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        updateWhiteStatusBar(requireActivity().window)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            loadData()

            moveToHanhwa.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url,
                    insurance = true,
                    insuranceType = "join"
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config)
                )
            }

            moveToDB.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url,
                    insurance = true,
                    insuranceType = "join"
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config)
                )
            }

            moveToInsuranceApply.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url,
                    insurance = true,
                    insuranceType = "apply"
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config)
                )
            }

            showErrorPopup.observeEvent(viewLifecycleOwner) { errorResponse ->
                when (errorResponse.code) {

                }
            }
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