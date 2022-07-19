package ai.comake.petping.ui.home.walk.adapter

import ai.comake.petping.R
import ai.comake.petping.data.vo.AudioGuideItem
import ai.comake.petping.databinding.WalkGuideListItemBinding
import ai.comake.petping.util.setSafeOnClickListener
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlin.reflect.KFunction3

class WalkGuideAdapter(
    private val onItemClicked: (AudioGuideItem) -> Unit,
    private val onDownloadClicked: KFunction3<String, String, Int, Unit>,
    private val context: Context
) :
    ListAdapter<AudioGuideItem, WalkGuideAdapter.ViewHolder>(TaskDiffCallback()) {
    lateinit var mRecyclerView: RecyclerView

    inner class ViewHolder(
        private val binding: WalkGuideListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            item: AudioGuideItem,
            onItemClicked: (AudioGuideItem) -> Unit,
            onDownloadClicked: (String, String, Int) -> Unit
        ) {
            binding.data = item
            binding.walkGuideStartButton.setSafeOnClickListener() { onItemClicked(item) }
            binding.walkGuideDownloadButton.setSafeOnClickListener {
                if (binding.downloadAudioGuideButtonText.text == "오디오 가이드 다운로드") {
                    onDownloadClicked(
                        item.audioFileUrl,
                        item.audioFileId.toString(),
                        layoutPosition
                    )
                }
            }

            binding.tagLayout.removeAllViews()

            item.tagList.forEach {
                val flexView = LayoutInflater.from(context)
                    .inflate(R.layout.flex_textview, binding.rootView, false)
                flexView.findViewById<TextView>(R.id.tag).text = "#${it}"
                binding.tagLayout.addView(flexView)
            }

            if (item.hasAudio) {
                binding.walkGuideDownloadButton.visibility = View.GONE
                binding.walkGuideStartButton.visibility = View.VISIBLE
            } else {
                binding.walkGuideStartButton.visibility = View.GONE
                binding.walkGuideDownloadButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun readyDownloadProgress(position: Int) {
        val view =
            mRecyclerView.layoutManager?.findViewByPosition(position)?.findViewById<TextView>(R.id.download_audio_guide_button_text)
        view?.text = "0 %"

        val downLoadIconProgressBar =
            mRecyclerView.layoutManager?.findViewByPosition(position)?.findViewById<ProgressBar>(R.id.walk_guide_downLoad_progressBar)
        downLoadIconProgressBar?.visibility = View.VISIBLE

        val downLoadIcon =
            mRecyclerView.layoutManager?.findViewByPosition(position)?.findViewById<TextView>(R.id.download_audio_guide_icon)
        downLoadIcon?.visibility = View.GONE
    }

    fun updateDownloadProgress(percent: Int, position: Int) {
        try {
            val downloadGuideButtonText =
                mRecyclerView.layoutManager?.findViewByPosition(position)?.findViewById<TextView>(R.id.download_audio_guide_button_text)
            downloadGuideButtonText?.text = "$percent %"
            val downLoadIconProgressBar =
                mRecyclerView.layoutManager?.findViewByPosition(position)?.findViewById<ProgressBar>(R.id.walk_guide_downLoad_progressBar)
            downLoadIconProgressBar?.progress = percent
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun updateDownloadComplete(position: Int) {
        try {
            currentList[position].hasAudio = true
            notifyItemChanged(position)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            WalkGuideListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position), onItemClicked, onDownloadClicked)
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<AudioGuideItem>() {
        override fun areItemsTheSame(
            oldItem: AudioGuideItem,
            newItem: AudioGuideItem
        ): Boolean {
            return oldItem.audioFileId == newItem.audioFileId
        }

        override fun areContentsTheSame(
            oldItem: AudioGuideItem,
            newItem: AudioGuideItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}