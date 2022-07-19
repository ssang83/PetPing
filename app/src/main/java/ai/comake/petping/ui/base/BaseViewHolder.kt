package ai.comake.petping.ui.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * android-petping-2
 * Class: BaseViewHolder
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
abstract class BaseViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position: Int)
}