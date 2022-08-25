package ai.comake.petping.ui.home

import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.Event
import ai.comake.petping.MainShareViewModel
import ai.comake.petping.R
import ai.comake.petping.data.db.badge.BadgeRepository
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.databinding.FragmentHomeBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.home.walk.WalkBottomUi
import ai.comake.petping.ui.home.walk.WalkViewModel
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._walkBottomUi
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.readTextFromUri
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var badgeRepository: BadgeRepository
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val args by navArgs<HomeFragmentArgs>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()
    private val homeShareViewModel: HomeShareViewModel by activityViewModels()
    private val walkViewModel by viewModels<WalkViewModel>()
    private var navController: NavController? = null
    private var menuIcons = hashMapOf(
        R.id.dashBoardScreen to BottomMenu(R.id.dashBoardScreenIcon, R.id.dashBoardScreenTitle),
        R.id.walkScreen to BottomMenu(R.id.walkScreenIcon, R.id.walkScreenTitle),
        R.id.rewardScreen to BottomMenu(R.id.rewardScreenIcon, R.id.rewardScreenTitle),
        R.id.shopScreen to BottomMenu(R.id.shopScreenIcon, R.id.shopScreenTitle),
        R.id.insuranceScreen to BottomMenu(R.id.insuranceScreenIcon, R.id.insuranceScreenTitle),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.log("TAG", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (navController == null) {
            binding = FragmentHomeBinding.inflate(inflater, container, false)
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            binding.event = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log("TAG", "args " + args.menulink)
        LogUtil.log("TAG", "userId : ${ID}")
        LogUtil.log("TAG", "token : ${AUTH_KEY}")

        if (navController == null) {
            setUpNavigation()
        }

        setUpObserver()
        setupClickEvent()
        checkMenuLink(args.menulink)
        checkNewBadge()

        LogUtil.log("screenName : ${homeShareViewModel.destinationScreen}")
    }

    fun checkNewBadge() {

    }

    fun onClickBottomNav(view: View) {
        when (view.id) {
            R.id.dashBoardScreen -> {
                LogUtil.log("TAG", "dashBoardScreen ")
                homeShareViewModel.destinationScreen = "dashBoardScreen"
                showDashBoardScreen()
            }
            R.id.walkScreen -> {
                LogUtil.log("TAG", "walkScreen ")
                homeShareViewModel.destinationScreen = "walkScreen"
                showWalkScreen()
            }
            R.id.rewardScreen -> {
                LogUtil.log("TAG", "rewardScreen ")
                homeShareViewModel.destinationScreen = "rewardScreen"
                showRewardScreen()

                if (isNewPointBadge()) {
                    insertPointBadge()
                }
            }
            R.id.shopScreen -> {
                LogUtil.log("TAG", "shopScreen ")
                homeShareViewModel.destinationScreen = "shopScreen"
                showShopScreen()
            }
            R.id.insuranceScreen -> {
                LogUtil.log("TAG", "insuranceScreen ")
                homeShareViewModel.destinationScreen = "insuranceScreen"
                showInsuranceScreen()
            }
        }

        changeUnSelectedMenuIcon(view.id)
    }

    private fun showDashBoardScreen() {
        navController?.navigate(R.id.dashBoardScreen)
        changeUnSelectedMenuIcon(R.id.dashBoardScreen)
    }

    private fun showWalkScreen() {
        navController?.navigate(R.id.walkScreen)
        changeUnSelectedMenuIcon(R.id.walkScreen)
    }

    private fun showRewardScreen() {
        navController?.navigate(R.id.rewardScreen)
        changeUnSelectedMenuIcon(R.id.rewardScreen)
    }

    private fun showShopScreen() {
        navController?.navigate(R.id.shopScreen)
        changeUnSelectedMenuIcon(R.id.shopScreen)
    }

    private fun showInsuranceScreen() {
        navController?.navigate(R.id.insuranceScreen)
        changeUnSelectedMenuIcon(R.id.insuranceScreen)
    }

    private fun showEtcScreen() {
        requireActivity().findNavController(R.id.nav_main)
            .navigate(R.id.etcFragment)
        showDashBoardScreen()
    }

    private fun changeUnSelectedMenuIcon(menuId: Int) {
        activity?.let { activity ->
            for ((key, value) in menuIcons) {
                val iconId = value.iconId
                val titleId = value.titleId
                if (key == menuId) {
                    binding.layoutHomeBottomNav.root.findViewById<ImageView>(iconId).isSelected =
                        true
                    binding.layoutHomeBottomNav.root.findViewById<TextView>(titleId)
                        .setTextColor(ContextCompat.getColor(activity, R.color.color_ff4857))
                } else {
                    binding.layoutHomeBottomNav.root.findViewById<ImageView>(iconId).isSelected =
                        false
                    binding.layoutHomeBottomNav.root.findViewById<TextView>(titleId)
                        .setTextColor(ContextCompat.getColor(activity, R.color.color_444444))
                }
            }

            if (viewModel.isStartWalk.value == true) {
                if (menuId == R.id.walkScreen) {
                    binding.layoutHomeBottomNav.walkingStatusIcon.background =
                        activity.getDrawable(R.drawable.walking_entering_nav_icon)
                } else {
                    binding.layoutHomeBottomNav.walkingStatusIcon.background =
                        activity.getDrawable(R.drawable.walking_leave_nav_icon)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    private fun setUpNavigation() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_home_fragment)
        navController = navHostFragment?.findNavController()

        navController?.apply {
            navigatorProvider.addNavigator(
                HomeBottomNavigator(
                    onBackPressedCallback,
                    R.id.nav_host_home_fragment,
                    childFragmentManager
                )
            )
            setGraph(R.navigation.home_navigation)
        }

        navController?.addOnDestinationChangedListener { _controller, destination, arguments ->
            LogUtil.log("TAG", "destination: $destination")
        }

        changeUnSelectedMenuIcon(R.id.dashBoardScreen)
    }

    private fun checkMenuLink(args: MenuLink?) {
        LogUtil.log("TAG", "args: ${args.toString()}")
        when (args) {
            is MenuLink.Fcm -> {
                when (args.type) {
                    "walk" -> {
                        showWalkScreen()
                    }
                    "reward" -> {
                        showRewardScreen()
                    }
                    "pingshop" -> {
                        showShopScreen()
                    }
                    "etc", "other", "event" -> {
                        showEtcScreen()
                    }
                }
            }

            is MenuLink.PetPing -> {
                when (args.type) {
                    "walk" -> {
                        showWalkScreen()
                    }
                }
//            else -> {
//                LogUtil.log("TAG","binding.homeBottomNavigation.selectedItemId")
//                binding.homeBottomNavigation.selectedItemId = R.id.dashBoardScreen
//            }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backKeyEvent()
        }
    }

    fun backKeyEvent() {
        LogUtil.log(
            "TAG",
            "homeShareViewModel.destinationScreen: ${homeShareViewModel.destinationScreen}"
        )
        LogUtil.log(
            "TAG",
            "homeShareViewModel.isVisibleAudioGuideList: ${homeShareViewModel.isVisibleAudioGuideList}"
        )
        if (homeShareViewModel.destinationScreen == "walkScreen" && homeShareViewModel.isVisibleAudioGuideList) {
            homeShareViewModel.showAudioGuideList.postValue(Event(false))
            return
        }

        when(_walkBottomUi.value) {
            WalkBottomUi.MARKING ->{
                _walkBottomUi.value = WalkBottomUi.NONE
                return
            }

            WalkBottomUi.PLACE ->{
                _walkBottomUi.value = WalkBottomUi.NONE
                return
            }

            WalkBottomUi.CLUSTER ->{
                _walkBottomUi.value = WalkBottomUi.NONE
                return
            }
        }

        finishApplication()
    }

    private fun setupClickEvent() {
        activity?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
//        binding.navHostHomeFragment.setOnClickListener() {
//            LogUtil.log("TAG", "setupClickEvent")
//        }
    }

    private fun setUpObserver() {
        homeShareViewModel.isVisibleBottomNavigation.observeEvent(
            viewLifecycleOwner,
            { isVisibile ->
                LogUtil.log("TAG", "isVisibile " + isVisibile)
                if (isVisibile) {
                    binding.layoutHomeBottomNav.root.visibility = View.VISIBLE
                } else {
                    binding.layoutHomeBottomNav.root.visibility = View.GONE
                }
            })

        viewModel.isStartWalk.observe(viewLifecycleOwner) { isStartWalk ->
            LogUtil.log("TAG", "isStartWalk: $isStartWalk")
            if (isStartWalk) {
                binding.layoutHomeBottomNav.walkingStatusIcon.visibility = View.VISIBLE
            } else {
                binding.layoutHomeBottomNav.walkingStatusIcon.visibility = View.GONE
            }
        }

        homeShareViewModel.moveToWalk.observeEvent(viewLifecycleOwner) {
            showWalkScreen()
            changeUnSelectedMenuIcon(R.id.walkScreen)
        }

        homeShareViewModel.moveToReward.observeEvent(viewLifecycleOwner) {
            showRewardScreen()
            changeUnSelectedMenuIcon(R.id.rewardScreen)
        }

        mainShareViewModel.isSucceedBadge.observe(viewLifecycleOwner, { result ->
            LogUtil.log("TAG", "isSucceedBadge: $result")
            if (isNewPointBadge()) {
                binding.layoutHomeBottomNav.rewardNewBadgeIcon.visibility = View.VISIBLE
            }
        })

        mainShareViewModel.moveLinkedScreen.observeEvent(viewLifecycleOwner, { menuLink ->
            checkMenuLink(menuLink)
        })

//        dashboardViewModel.openWidzet.observe(
//            viewLifecycleOwner, EventObserver
//            { isClick ->
//                findNavController().navigate(R.id.action_dashBoardScreen_to_widgetScreen)
//            }
//        )
    }

    fun insertPointBadge() {
        if (mainShareViewModel.remoteBadge != null && mainShareViewModel.localBadge != null) {
            mainShareViewModel.localBadge!!.newMissionId =
                mainShareViewModel.remoteBadge?.newMissionId
            mainShareViewModel.localBadge!!.newSaveRewardId =
                mainShareViewModel.remoteBadge!!.newSaveRewardId
            viewLifecycleOwner.lifecycleScope.launch() {
                badgeRepository.insert(mainShareViewModel.localBadge!!)
                binding.layoutHomeBottomNav.rewardNewBadgeIcon.visibility = View.GONE
            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data = result.data?.data
                LogUtil.log("TAG", " " + readTextFromUri(requireContext(), data!!))
            }
        }

    /**
     * '뒤로가기' 버튼 2회 연속 입력을 통한 종료를 사용자에게 안내하고 처리
     */
    private var backPressedTime: Long = 0
    private fun finishApplication() {
        if (System.currentTimeMillis() - backPressedTime < DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT) {
            activity?.finish()
            return
        }
        Toast.makeText(activity, getString(R.string.finish_app_guide), Toast.LENGTH_SHORT)
            .show()
        backPressedTime = System.currentTimeMillis()
    }

    private fun fileSelect() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {    // 1
            addCategory(Intent.CATEGORY_OPENABLE)   // 2
            type = "text/*"    // 3
        }

        resultLauncher.launch(intent)
    }

    //포인트,리워드 NEW 배지 확인
    fun isNewPointBadge(): Boolean {
        if (mainShareViewModel.remoteBadge != null && mainShareViewModel.localBadge != null) {
            return mainShareViewModel.remoteBadge?.newMissionId!! > mainShareViewModel.localBadge?.newMissionId!! ||
                    mainShareViewModel.remoteBadge!!.newSaveRewardId != mainShareViewModel.localBadge!!.newSaveRewardId
        } else {
            return false
        }
    }

    companion object {
        const val TAG = "HomeFragment"
    }

    data class BottomMenu(
        val iconId: Int = 0,
        val titleId: Int = 0
    ) {
    }
}
