package ai.comake.petping.ui.home

import ai.comake.petping.*
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.databinding.FragmentHomeBinding
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.readTextFromUri
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val args by navArgs<HomeFragmentArgs>()
    private val mainShareViewModel: MainShareViewModel by activityViewModels()
    private val homeShareViewModel: HomeShareViewModel by activityViewModels()
    private var navController: NavController? = null
    private var menuIcons = hashMapOf(
        R.id.dashBoardScreen to R.id.dashBoardScreenIcon,
        R.id.walkScreen to R.id.walkScreenIcon,
        R.id.rewardScreen to R.id.rewardScreenIcon,
        R.id.shopScreen to R.id.shopScreenIcon,
        R.id.insuranceScreen to R.id.insuranceScreenIcon,
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
        if (navController == null) {
            setUpNavigation()
            setupClickEvent()
            setUpObserver()
            checkMenuLink(args.menulink)

            LogUtil.log("TAG", "args " + args.menulink)
            LogUtil.log("TAG", "userId : ${AppConstants.ID}")
            LogUtil.log("TAG", "token : ${AppConstants.AUTH_KEY}")
            LogUtil.log("TAG", "loginHeader visible : ${AppConstants.LOGIN_HEADER_IS_VISIBLE}")
            LogUtil.log("TAG", "profileHeader visible : ${AppConstants.PROFILE_HEADER_IS_VISIBLE}")
        }
    }

    fun onClickBottomNav(view: View) {
        when (view.id) {
            R.id.dashBoardScreen -> {
                LogUtil.log("TAG", "dashBoardScreen ")
                showDashBoardScreen()
            }
            R.id.walkScreen -> {
                LogUtil.log("TAG", "walkScreen ")
                showWalkScreen()
            }
            R.id.rewardScreen -> {
                LogUtil.log("TAG", "rewardScreen ")
                showRewardScreen()
            }
            R.id.shopScreen -> {
                LogUtil.log("TAG", "shopScreen ")
                showShopScreen()
            }
            R.id.insuranceScreen -> {
                LogUtil.log("TAG", "insuranceScreen ")
                showInsuranceScreen()
            }
        }

        changeUnSelectedMenuIcon(view.id)
    }

    private fun showDashBoardScreen() {
        navController?.navigate(R.id.dashBoardScreen)
    }

    private fun showWalkScreen() {
        navController?.navigate(R.id.walkScreen)
    }

    private fun showRewardScreen() {
        navController?.navigate(R.id.rewardScreen)
    }

    private fun showShopScreen() {
        navController?.navigate(R.id.shopScreen)
    }

    private fun showInsuranceScreen() {
        navController?.navigate(R.id.insuranceScreen)
    }

    private fun changeUnSelectedMenuIcon(menuId: Int) {
        for ((key, value) in menuIcons) {
            binding.layoutHomeBottomNav.root.findViewById<ImageView>(value).isSelected = key == menuId
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
                BottomNavigator(
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
//

    }

    private fun checkMenuLink(args: MenuLink?) {
        when (args) {
//            is DeepLink.Fcm -> {
//                LogUtil.log("TAG", "args.type " + args.type)
//                bottomNavigationView.selectedItemId = R.id.walkScreen
//            }
//            is DeepLink.Airbridge -> {
//                LogUtil.log("TAG", "args.type " + args.type)
//                bottomNavigationView.selectedItemId = R.id.walkScreen
//            }
            is MenuLink.PetPing -> {
                when (args.type) {
                    "walk" -> {
//                        binding.homeBottomNavigation.selectedItemId = R.id.walkScreen
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
            LogUtil.log("TAG", "handleOnBackPressed")
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

        mainShareViewModel.menuLink.observeEvent(viewLifecycleOwner, { menuLink ->
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
                LogUtil.log("TAG", " " + readTextFromUri(context!!, data!!))
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
}