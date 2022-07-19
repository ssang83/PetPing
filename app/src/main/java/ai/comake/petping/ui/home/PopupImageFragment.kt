package ai.comake.petping.ui.home

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.R
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentPopupImageBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.util.LogUtil
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes

/**
 * android-petping-2
 * Class: PopupImageFragment
 * Created by cliff on 2022/06/20.
 *
 * Description:
 */

const val EXTRA_IMAGE_URL = "image_url"
const val EXTRA_LINK_URL = "link_url"

class PopupImageFragment(
    private val callback: (() -> Unit)? = null
) : BaseFragment<FragmentPopupImageBinding>(FragmentPopupImageBinding::inflate) {

    private val viewModel: PopupImageViewModel by viewModels()

    private val url by lazy {
        arguments?.getString(EXTRA_IMAGE_URL)
            ?: throw IllegalStateException("url must set for starting PopupImageFragment")
    }

    private val linkUrl by lazy {
        arguments?.getString(EXTRA_LINK_URL)
            ?: throw IllegalStateException("linkUrl must set for starting PopupImageFragment")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        LogUtil.log("image url : $url, link url : $linkUrl")

        with(viewModel) {

            init(url, linkUrl)

            moveToWeb.observeEvent(viewLifecycleOwner) { url ->
                AirbridgeManager.trackEvent(
                    "homeviewd_homepopup1_click",
                    "homeviewd_homepopup1_click_action",
                    "homeviewd_homepopup1_click_label"
                )

                val config = WebConfig(
                    url = url
                )
                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config))

                callback?.invoke()
            }
        }
    }
}