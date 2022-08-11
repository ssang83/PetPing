package ai.comake.petping.ui.home.shop

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.R
import ai.comake.petping.data.vo.GodoMallConfig
import ai.comake.petping.data.vo.WebConfig
import ai.comake.petping.databinding.FragmentShopBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.common.widget.OnPageChangeCallbackForInfiniteIndicator
import ai.comake.petping.ui.history.reward.FROM_SHOP
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.updateWhiteStatusBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopFragment : Fragment(){
    private lateinit var binding: FragmentShopBinding
    private val viewModel by viewModels<ShopViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log("TAG", "")
        updateWhiteStatusBar(requireActivity().window)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        with(viewModel) {

            bannerUpdate.observeEvent(viewLifecycleOwner) {
                setUI()
            }

            signUpErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C7014" -> {
                        SingleBtnDialog(
                            requireContext(),
                            errorBody.message,
                            getString(R.string.pet_ping_shop_no_auth_popup_desc),
                        ) {
                            requireActivity().findNavController(R.id.nav_main)
                                .navigate(R.id.action_homeScreen_to_certificationFragment)
                        }.show()
                    }

                    "C7999" -> {
                        SingleBtnDialog(
                            requireContext(),
                            requireContext().getString(R.string.pet_ping_shop_service_check),
                            errorBody.message
                        ).show()
                    }
                }
            }

            shopItemsErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                when (errorBody.code) {
                    "C0020" -> {
                        requireActivity().findNavController(R.id.nav_main)
                            .navigate(HomeFragmentDirections.actionHomeScreenToProfileGraph(false))
                    }
                    else -> {
                        SingleBtnDialog(
                            requireContext(),
                            errorBody.title,
                            errorBody.message
                        ).show()
                    }
                }
            }

            moveToGodoMall.observeEvent(viewLifecycleOwner) { url ->
                val config = GodoMallConfig(
                    url = url,
                    productUrl = ""
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToGodoMallFragment(config))

            }

            moveToProduct.observeEvent(viewLifecycleOwner) { url ->
                val config = GodoMallConfig(
                    url = godoUrl,
                    productUrl = url
                )

                requireActivity().findNavController(R.id.nav_main)
                    .navigate(HomeFragmentDirections.actionHomeScreenToGodoMallFragment(config))
            }

            moveToPingRecord.observeEvent(viewLifecycleOwner) {
                AirbridgeManager.trackEvent(
                    "rewardviewed_upper_detailed_click",
                    "rewardviewed_upper_detailed_click_action",
                    "rewardviewed_upper_detailed_click_label",
                )

                bundleOf(FROM_SHOP to true).let {
                    requireActivity().findNavController(R.id.nav_main).navigate(
                        R.id.action_homeScreen_to_rewardHistoryFragment,
                        it
                    )
                }
            }

            moveToBannerDetail.observeEvent(viewLifecycleOwner) { url ->
                val config = WebConfig(
                    url = url
                )

                requireActivity().findNavController(R.id.nav_main).navigate(
                    HomeFragmentDirections.actionHomeScreenToContentsWebFragment(config)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.bannerItems.size > 0) {
            setUI()
        }
    }

    private fun setUI() {
        with(binding) {
            viewPager.apply {
                registerOnPageChangeCallback(
                    OnPageChangeCallbackForInfiniteIndicator(
                        requireActivity(),
                        viewModel!!.bannerItems,
                        viewPager.currentItem
                    )
                )
            }
        }
    }

    companion object {
        const val TAG = "ShopFragment"
    }
}