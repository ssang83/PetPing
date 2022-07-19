package ai.comake.petping.ui.common.dialog

import ai.comake.petping.R
import ai.comake.petping.util.recyclerViewSetup
import ai.comake.petping.util.setSafeOnClickListener
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * android-petping-2
 * Class: SelectBottomDialog
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
class SelectBottomDialogFragment(
    private val title: String,
    private val btnText: String,
    private val dataList: List<String>,
    private val initial: String,
    private val listener: (String) -> Unit
) : BottomSheetDialogFragment() {

    var ret = initial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_dialog_selector, container, false)
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.title).text = this.title
        view.findViewById<Button>(R.id.btn).text = this.btnText

        if (initial != "") {
            view.findViewById<Button>(R.id.btn).isEnabled = true
        }

        val walkPetAdapter = SelectorAdapter(dataList, initial) {
            ret = it
            view.findViewById<Button>(R.id.btn).isEnabled = true
        }
        recyclerViewSetup(context!!, view.findViewById(R.id.recycler_view), walkPetAdapter)
        view.findViewById<Button>(R.id.btn).setSafeOnClickListener {
            listener.invoke(ret)
            dismiss()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutParams = recyclerView.layoutParams
        val headerHeight = (67 * resources.displayMetrics.density).toInt()
        val buttonHeight = (56 * resources.displayMetrics.density).toInt()
        val calculationHeight = headerHeight + buttonHeight + (dataList.size * 69 * resources.displayMetrics.density).toInt()

        if(calculationHeight > (resources.displayMetrics.heightPixels - (126 * resources.displayMetrics.density).toInt())) {
            layoutParams.height = (resources.displayMetrics.heightPixels  - headerHeight - buttonHeight - (126 * resources.displayMetrics.density).toInt())
            recyclerView.layoutParams = layoutParams
            recyclerView.requestLayout()
        } else {
            layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
            recyclerView.layoutParams = layoutParams
            recyclerView.requestLayout()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }
}