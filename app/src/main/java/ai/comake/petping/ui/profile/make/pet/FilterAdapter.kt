package ai.comake.petping.ui.profile.make.pet

import ai.comake.petping.R
import ai.comake.petping.databinding.ItemBreedFilterBinding
import ai.comake.petping.ui.base.BindingViewHolder
import ai.comake.petping.util.LogUtil
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: FilterAdapter
 * Created by cliff on 2022/06/14.
 *
 * Description:
 */
class FilterAdapter(
    private val viewModel: ProfileSharedViewModel,
    private val breedList: List<String>
): RecyclerView.Adapter<FilterAdapter.ViewHolder>(), Filterable {

    private var unfilterList:List<String> = breedList
    private var filterList:List<String> = breedList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_breed_filter,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filterList[position])
    }

    override fun getItemCount() = filterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val str = constraint.toString()
                if (str.isEmpty()) {
                    filterList = unfilterList
                } else {
                    val filteringList = arrayListOf<String>()
                    for (item in unfilterList) {
                        if (item.lowercase().contains(str)) {
                            filteringList.add(item)
                        }
                    }

                    filterList = filteringList
                }

                val filterResults = FilterResults()
                filterResults.values = filterList

                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filterList = results.values as List<String>
                notifyDataSetChanged()
            }
        }
    }

    inner class ViewHolder(view: View) : BindingViewHolder<ItemBreedFilterBinding>(view) {
        fun bind(item: String) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }
}