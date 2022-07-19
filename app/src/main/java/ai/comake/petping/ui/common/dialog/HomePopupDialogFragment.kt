package ai.comake.petping.ui.common.dialog

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.App
import ai.comake.petping.R
import ai.comake.petping.data.vo.Popup
import ai.comake.petping.databinding.BottomDialogHomePopupBinding
import ai.comake.petping.ui.home.EXTRA_IMAGE_URL
import ai.comake.petping.ui.home.EXTRA_LINK_URL
import ai.comake.petping.ui.home.PopupImageFragment
import ai.comake.petping.util.setSafeOnClickListener
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

/**
 * android-petping-2
 * Class: HomePopupDialogFragment
 * Created by cliff on 2022/06/20.
 *
 * Description:
 */
class HomePopupDialogFragment(
    popupList: List<Popup>
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomDialogHomePopupBinding

    private val preference by lazy {
        App.getPrefernece(requireContext())
    }

    private var homePopupList = popupList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val d: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogHomePopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            if (homePopupList.size > 0) {
                viewPager.apply {
                    adapter = PopupImagePages()
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL
                }
            }

            indicator.apply {
                when {
                    homePopupList.size == 1 -> visibility = View.GONE
                    else -> {
                        visibility = View.VISIBLE
                        TabLayoutMediator(indicator, viewPager) { tab, position ->}.attach()
                    }
                }
            }

            doNotShow.setSafeOnClickListener {
                AirbridgeManager.trackEvent(
                    "homeviewd_homepopup2_click",
                    "homeviewd_homepopup2_click_action",
                    "homeviewd_homepopup2_click_label"
                )

                homePopupList.forEach { popup ->
                    preference.setDoNotShowPopup(popup.id)
                }

                dismiss()
            }

            close.setSafeOnClickListener {
                AirbridgeManager.trackEvent(
                    "homeviewd_homepopup3_click",
                    "homeviewd_homepopup3_click_action",
                    "homeviewd_homepopup3_click_label"
                )

                homePopupList.forEach { popup ->
                    preference.setClosePopup(popup.id)
                }

                dismiss()
            }
        }
    }

    inner class PopupImagePages : FragmentStateAdapter(this) {

        override fun getItemCount() = homePopupList.size
        override fun createFragment(position: Int) =
            PopupImageFragment(
                callback = {
                    homePopupList.forEach { popup ->
                        preference.setClosePopup(popup.id)
                    }
                    dismiss()
                }
            ).apply {
                bundleOf(
                    EXTRA_IMAGE_URL to homePopupList[position].popupImageURL,
                    EXTRA_LINK_URL to homePopupList[position].linkURL
                ).let { arguments = it }
            }
    }
}