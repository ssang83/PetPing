package ai.comake.petping.ui.etc.member_info

import ai.comake.petping.R
import ai.comake.petping.data.vo.PersonalLocationInformationInquiryLog
import ai.comake.petping.databinding.ItemLocationHistoryBinding
import ai.comake.petping.ui.base.BindingViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * android-petping-2
 * Class: LocationHistoryAdapter
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
class LocationHistoryAdapter(
    private val viewModel:LocationHistoryViewModel
): PagingDataAdapter<PersonalLocationInformationInquiryLog, LocationHistoryAdapter.ViewHolder>(DATA_COMPARETOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_location_history,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemLocationHistoryBinding>(view) {
        fun bind(item: PersonalLocationInformationInquiryLog) {
            binding.item = item
            binding.viewmodel = viewModel
        }
    }

    companion object {
        private val DATA_COMPARETOR = object : DiffUtil.ItemCallback<PersonalLocationInformationInquiryLog>() {
            override fun areItemsTheSame(oldItem: PersonalLocationInformationInquiryLog, newItem: PersonalLocationInformationInquiryLog): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PersonalLocationInformationInquiryLog, newItem: PersonalLocationInformationInquiryLog): Boolean =
                oldItem == newItem
        }
    }
}