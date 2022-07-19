package ai.comake.petping

import ai.comake.petping.data.vo.LogoutEvent
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.data.vo.NetworkErrorEvent
import ai.comake.petping.databinding.ActivityMainBinding
import ai.comake.petping.util.LogUtil
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainShareViewModel: MainShareViewModel by viewModels()
    private var destinationScreen = "mainScreen"
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation(window.decorView)
        setUpObserver()
        LogUtil.log("TAG", "")
    }

    private fun setUpNavigation(view: View) {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_main
        ) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _controller, destination, arguments ->
            LogUtil.log("TAG", "destination " + destination.label)
            destinationScreen = destination.label.toString()
        }
    }

    private fun setUpObserver() {
        mainShareViewModel.showPopUp.observeEvent(this) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                dismissLoadingDialog()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogUtil.log("TAG", "")
        checkMenuDirection(intent)
    }

    private fun checkMenuDirection(intent: Intent?) {
        LogUtil.log("TAG", "")
        if (intent?.hasExtra("menu") == true) {
            if (destinationScreen == "homeScreen") {
                openHomeNavMenu(intent.getStringExtra("menu") ?: "")
            } else {
                openMainNavMenu(intent.getStringExtra("menu") ?: "")
            }
        }
    }

    private fun openHomeNavMenu(menuName: String) {
        when (menuName) {
            "walk" -> {
                mainShareViewModel.setMenuLink(MenuLink.PetPing("walk"))
            }
        }
    }

    private fun openMainNavMenu(menuName: String) {
        when (menuName) {
            "walk" -> {
                val arg = MenuLink.PetPing("walk")
                findNavController(R.id.nav_main).navigate(
                    NavMainDirections.actionMainToHome().setMenulink(arg)
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    /**
     * 에디트 텍스트 있는 경우 화면 터치 할 경우 clear focus 및 키보드 숨김
     *
     * @param event
     * @return
     */
//    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
//        if (event!!.getAction() == MotionEvent.ACTION_DOWN) {
//            val v = currentFocus
//            if (v is EditText) {
//                val outRect = Rect()
//                v.getGlobalVisibleRect(outRect)
//                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
//                    v.clearFocus()
//                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event)
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: NetworkErrorEvent) {
        LogUtil.log("TAG","")
        Toast.makeText(this, "네트워크 에러", Toast.LENGTH_SHORT).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: LogoutEvent) {
        LogUtil.log("TAG","LogoutEvent")
        Toast.makeText(this, "로그아웃 ${event.code}", Toast.LENGTH_SHORT).show()
//        if (event.code == 401) { // 자동 로그아웃
//            SingleBtnDialog(
//                this,
//                getString(R.string.logout_title),
//                getString(R.string.auto_logout_desc)
//            ) {
//                AppConstants.LOGIN_HEADER_IS_VISIBLE = true
//                AppConstants.PROFILE_HEADER_IS_VISIBLE = true
//                AppConstants.AUTH_KEY = ""
//                AppConstants.ID = ""
//            }.show()
//        } else if (event.code == 500) {
////            SingleBtnDialog(
////                this,
////                getString(R.string.server_error_title),
////                getString(R.string.server_error_desc)
////            ) {
////                AppConstants.LOGIN_HEADER_IS_VISIBLE = true
////                AppConstants.PROFILE_HEADER_IS_VISIBLE = true
////                AppConstants.AUTH_KEY = ""
////                AppConstants.ID = ""
////            }.show()
//        }
    }

    fun showLoadingDialog() {
        binding.loadingDialog.root.visibility = View.VISIBLE
    }

    fun dismissLoadingDialog() {
        binding.loadingDialog.root.visibility = View.GONE
    }
}