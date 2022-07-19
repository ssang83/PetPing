package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.*
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentFaqBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.etc.notice.NoticeFragmentDirections
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: FAQFragment
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@AndroidEntryPoint
class FAQFragment : BaseFragment<FragmentFaqBinding>(FragmentFaqBinding::inflate) {

    private val viewModel: FAQViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

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

            appBarScroll.observeEvent(viewLifecycleOwner) {
                (parentFragment as QuestionFragment).scrollTop()
            }
        }
    }
}