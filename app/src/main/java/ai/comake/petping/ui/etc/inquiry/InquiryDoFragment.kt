package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.*
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentInquiryDoBinding
import ai.comake.petping.ui.base.BaseFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: InquiryDoFragment
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@AndroidEntryPoint
class InquiryDoFragment :
    BaseFragment<FragmentInquiryDoBinding>(FragmentInquiryDoBinding::inflate) {

    private val viewModel: InquiryDoViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            loadData()

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when(state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            moveToDetail.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(QuestionFragmentDirections.actionQuestionFragmentToContentsWebFragment(config))
            }
        }
    }
}