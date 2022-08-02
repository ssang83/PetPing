package ai.comake.petping.ui.home.insurance

import ai.comake.petping.databinding.FragmentInsuranceBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

            moveToInsurance.observeEvent(viewLifecycleOwner) {
                //TODO : 펫보험 바로가기
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