package ai.comake.petping.ui.history.reward.saving

import ai.comake.petping.databinding.FragmentSavingBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.LogUtil
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: SavingFragment
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
@AndroidEntryPoint
class SavingFragment : BaseFragment<FragmentSavingBinding>(FragmentSavingBinding::inflate) {

    private val viewModel by viewModels<SavingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            getSavingPoints()
            getExpirationPoints()

            updatePoint.observeEvent(viewLifecycleOwner) {
                updatePoints()
            }
        }

        setup()
    }

    private fun setup() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.savingListRecyclerView.let {
            it.layoutManager = layoutManager
            it.itemAnimator = DefaultItemAnimator()
            it.adapter = SavingItemAdapter(viewModel)
        }
    }

    private fun updatePoints() {
        binding.savingListRecyclerView.apply {
            viewModel.savingPointData?.let { ongoing ->
                viewModel.expiredPointData?.let { completion ->
                    (adapter as SavingItemAdapter).updateData(
                        viewModel.savingHistories,
                        ongoing.listSize.toInt(),
                        viewModel.expiredHistories,
                        completion.listSize.toInt()
                    )
                }
            }

            adapter?.notifyDataSetChanged()
        }
    }
}