package ai.comake.petping.ui.history.reward.using

import ai.comake.petping.databinding.FragmentUsingBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: UsingFragment
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
@AndroidEntryPoint
class UsingFragment : BaseFragment<FragmentUsingBinding>(FragmentUsingBinding::inflate) {

    private val viewModel by viewModels<UsingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            getUsingPoints()

            updatePoint.observeEvent(viewLifecycleOwner) {
                updatePoints()
            }
        }

        setup()
    }

    private fun setup() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.usingListRecyclerView.let {
            it.layoutManager = layoutManager
            it.itemAnimator = DefaultItemAnimator()
            it.adapter = UsingItemAdapter(viewModel)
        }
    }

    private fun updatePoints() {
        binding.usingListRecyclerView.let {
            viewModel.usingPointData?.let { using ->
                (it.adapter as UsingItemAdapter).updateData(
                    viewModel.usingHistories,
                    using.listSize.toInt()
                )
            }

            it.adapter?.notifyDataSetChanged()
        }
    }
}