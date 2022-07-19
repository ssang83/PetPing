package ai.comake.petping.ui.etc.member_info

import ai.comake.petping.*
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.databinding.FragmentLocationHistoryBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.itemdecoration.MyDividerItemDecoration
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: LocationHistoryFragment
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
@AndroidEntryPoint
class LocationHistoryFragment :
    BaseFragment<FragmentLocationHistoryBinding>(FragmentLocationHistoryBinding::inflate) {

    private val viewModel: LocationHistoryViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    @Inject
    lateinit var userDataRepository: UserDataRepository

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
        }

        setPagingData()
    }

    private fun setPagingData() {
        with(binding) {
            recyclerHistory.apply {
                adapter = LocationHistoryAdapter(viewModel!!)
                addItemDecoration(MyDividerItemDecoration(requireContext(), R.drawable.notice_divider))
            }

            lifecycleScope.launch {
                val source = LocationHistoryDataSource(userDataRepository, viewModel!!)
                val pagingData = Pager(
                    config = PagingConfig(enablePlaceholders = false, pageSize = 10),
                    pagingSourceFactory = { source }
                ).flow

                pagingData.collectLatest { data ->
                    (recyclerHistory.adapter as LocationHistoryAdapter).submitData(data)
                }
            }

            header.btnBack.setSafeOnClickListener {
                requireActivity().backStack(R.id.nav_main)
            }
        }
    }
}