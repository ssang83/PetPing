package ai.comake.petping.ui.history.walk

import ai.comake.petping.*
import ai.comake.petping.data.repository.WalkRepository
import ai.comake.petping.databinding.FragmentWalkHistoryBinding
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.profile.DogProfileFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: WalkHistoryFragment
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
@AndroidEntryPoint
class WalkHistoryFragment(
    private val _petId:Int,
    private val _viewMode:String
) :
    BaseFragment<FragmentWalkHistoryBinding>(FragmentWalkHistoryBinding::inflate) {

    private val viewModel: WalkHistoryViewModel by viewModels()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()

    @Inject
    lateinit var walkRepository: WalkRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        with(viewModel) {

            uiState.observeEvent(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> mainShareViewModel.showPopUp.emit(true)
                    else -> mainShareViewModel.showPopUp.emit(false)
                }
            }

            showDeletePopup.observeEvent(viewLifecycleOwner) { walkId ->
                DoubleBtnDialog(
                    requireContext(),
                    getString(R.string.walk_record_delete_titile),
                    getString(R.string.walk_record_delete_message),
                    true,
                    okCallback = { viewModel.deleteWalkRecord(walkId) },
                ).show()
            }

            refreshHistory.observeEvent(viewLifecycleOwner) {
                (binding.historyList.adapter as WalkHistoryAdapter).refresh()
            }

            showImagePopup.observeEvent(viewLifecycleOwner) { imgUrl ->
                (parentFragment as DogProfileFragment).showFullImage(imgUrl)
            }

            appBarScroll.observeEvent(viewLifecycleOwner) {
                (parentFragment as DogProfileFragment).scrollTop()
            }

            moveToRoute.observeEvent(viewLifecycleOwner) { petId ->
                //TODO : 산책경로 화면으로 이동
            }
        }

        setPaginData()
    }

    private fun setPaginData() {
        binding.historyList.apply {
            adapter = WalkHistoryAdapter(viewModel)
        }

        lifecycleScope.launch {
            val pagingData = Pager(
                config = PagingConfig(enablePlaceholders = false, pageSize = 10),
                pagingSourceFactory = {
                    WalkHistoryDataSource(walkRepository, viewModel, _petId, _viewMode)
                }
            ).flow

            pagingData.collectLatest {
                (binding.historyList.adapter as WalkHistoryAdapter).submitData(it)
            }
        }
    }
}