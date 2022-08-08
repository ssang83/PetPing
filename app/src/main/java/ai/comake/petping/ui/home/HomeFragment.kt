package ai.comake.petping.ui.home

import ai.comake.petping.*
import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.DOUBLE_BACK_PRESS_EXITING_TIME_LIMIT
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.databinding.FragmentHomeBinding
import ai.comake.petping.observeEvent
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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val args by navArgs<HomeFragmentArgs>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()
    private val homeShareViewModel: HomeShareViewModel by viewModels()
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

        setUpObserver()
        setupClickEvent()

        if (navController == null) {
            setUpNavigation()
        }

        checkMenuLink(args.menulink)
    }

    fun onClickBottomNav(view: View) {
        when (view.id) {
            R.id.dashBoardScreen -> {
                LogUtil.log("TAG", "dashBoardScreen ")
                homeShareViewModel.screenName = "dashBoardScreen"
                showDashBoardScreen()
            }
            R.id.walkScreen -> {
                LogUtil.log("TAG", "walkScreen ")
                homeShareViewModel.screenName = "walkScreen"
                showWalkScreen()
            }
            R.id.rewardScreen -> {
                LogUtil.log("TAG", "rewardScreen ")
                homeShareViewModel.screenName = "rewardScreen"
                showRewardScreen()
            }
            R.id.shopScreen -> {
                LogUtil.log("TAG", "shopScreen ")
                homeShareViewModel.screenName = "shopScreen"
                showShopScreen()
            }
            R.id.insuranceScreen -> {
                LogUtil.log("TAG", "insuranceScreen ")
                homeShareViewModel.screenName = "insuranceScreen"
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

        changeUnSelectedMenuIcon(R.id.dashBoardScreen)

//        bottomNavigationView = view.findViewById(R.id.homeBottomNavigation)
//        bottomNavigationView.itemIconTintList = null
//        bottomNavigationView.setupWithNavController(navController)

//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.dashBoardScreen -> {
//                    true
//                }
//
//                R.id.walkScreen -> {
//                    findNavController().navigate(R.id.walkScreen)
//                    true
//                }
//                R.id.rewardScreen -> {
//                    true
//                }
//                R.id.shopScreen -> {
//                    true
//                }
//                R.id.insuranceScreen -> {
//                    true
//                }
//
//                else -> {
//                    false
//                }
//
//            }
//        }
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
                    "etc", "other", "event" ->{
                        showEtcScreen()
                    }
                }
            }
//            else -> {
//                LogUtil.log("TAG","binding.homeBottomNavigation.selectedItemId")
//                binding.homeBottomNavigation.selectedItemId = R.id.dashBoardScreen
//            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishApplication()
        }
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
//                if (isVisibile) {
//                    binding.homeBottomNavigation.visibility = View.VISIBLE
//                } else {
//                    binding.homeBottomNavigation.visibility = View.GONE
//                }
            })

        homeShareViewModel.moveToWalk.observeEvent(viewLifecycleOwner) {
            showWalkScreen()
            changeUnSelectedMenuIcon(R.id.walkScreen)
        }

        homeShareViewModel.moveToReward.observeEvent(viewLifecycleOwner) {
            showRewardScreen()
            changeUnSelectedMenuIcon(R.id.rewardScreen)
        }

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
        Toast.makeText(activity, getString(R.string.finish_app_guide), Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }

    private fun fileSelect() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {    // 1
            addCategory(Intent.CATEGORY_OPENABLE)   // 2
            type = "text/*"    // 3
        }

        resultLauncher.launch(intent)
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
