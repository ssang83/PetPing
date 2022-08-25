package ai.comake.petping

import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.AppConstants.ID
import ai.comake.petping.data.vo.LogoutEvent
import ai.comake.petping.data.vo.MenuLink
import ai.comake.petping.data.vo.NetworkErrorEvent
import ai.comake.petping.databinding.ActivityMainBinding
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainShareViewModel: MainShareViewModel by viewModels()
    private var networkErrorDialog: SingleBtnDialog? = null

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation(window.decorView)
        setUpObserver()

        ID = sharedPreferencesManager.getDataStoreLoginId()
        AUTH_KEY = sharedPreferencesManager.getDataStoreAccessToken()

        FirebaseApp.initializeApp(this)

        checkSavedFcmToken()

        LogUtil.log("TAG", "")
    }

    private fun checkSavedFcmToken() {
        if (!sharedPreferencesManager.hasFCMTokenDataStore()) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result
                LogUtil.log("TAG", "FCM token $token")
                if (token != null) {
                    sharedPreferencesManager.saveFCMTokenDataStore(token)
                }
            })
        }
    }

    private fun checkNewBadge() {
        mainShareViewModel.asyncNewBadge()
    }

    private fun setUpNavigation(view: View) {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_main
        ) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _controller, destination, arguments ->
            LogUtil.log("TAG", "destination " + destination.label)
            mainShareViewModel.destinationScreen = destination.label.toString()

            when (mainShareViewModel.destinationScreen) {
                "mainScreen" -> {
                    updateWhiteIconStatusBar(this,Color.parseColor("#FF4857"))
                }
                "homeScreen" -> {
                    updateBlackIconStatusBar(this,Color.parseColor("#FFFFFF"))
                    checkNewBadge()
                }
                "loginScreen" -> {
                    updateBlackIconStatusBar(this,Color.parseColor("#FFFFFF"))
                }
            }

            if (mainShareViewModel.destinationScreen == "mainScreen") {
                window?.apply {
                    //상태바
                    statusBarColor = Color.parseColor("#FF4857")
                    //상태바 아이콘(true: 검정 / false: 흰색)
                    WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars =
                        false
                }
            }

            if (mainShareViewModel.destinationScreen == "homeScreen") {
                window?.apply {
                    //상태바
                    statusBarColor = Color.parseColor("#FFFFFF")
                    //상태바 아이콘(true: 검정 / false: 흰색)
                    WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars =
                        true
                }
            }
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

        mainShareViewModel.registFcmToken.observeEvent(this) { it ->
            LogUtil.log("TAG", "registFcmToken: $it")
            mainShareViewModel.registFCMToken()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        LogUtil.log("TAG", "")
        checkMenuDirection(intent)
    }

    private fun checkMenuDirection(intent: Intent?) {
        LogUtil.log("TAG", "")
        if (intent?.hasExtra("type") == true) {
            val type = intent.getStringExtra("type") ?: ""
            val link = intent.getStringExtra("link") ?: ""

            LogUtil.log("TAG", "type: $type")
            LogUtil.log("TAG", "link: $link")

            mainShareViewModel.menuLinkFcm = MenuLink.Fcm(type, link)
            openHomeNavMenu(mainShareViewModel.menuLinkFcm)
        } else if (intent?.hasExtra("menu") == true) {
            val type = intent.getStringExtra("menu") ?: ""

            LogUtil.log("TAG", "type: $type")

            mainShareViewModel.menuLinkPetPing = MenuLink.PetPing(type)
            openHomeNavMenu(mainShareViewModel.menuLinkPetPing)
        }
    }

    private fun openHomeNavMenu(menuLink: MenuLink) {
        if (mainShareViewModel.destinationScreen == "homeScreen") {
            mainShareViewModel.moveLinkedScreen.value = Event(menuLink)
        } else {
            findNavController(R.id.nav_main).navigate(
                MainFragmentDirections.actionMainToHome().setMenulink(menuLink)
            )
        }
    }

    private fun openMainNavMenu(menuName: String) {
        when (menuName) {
            "walk" -> {
                val arg = MenuLink.PetPing("walk")
                findNavController(R.id.nav_main).navigate(
                    MainFragmentDirections.actionMainToHome().setMenulink(arg)
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
    fun onEventMainThread(event: NetworkErrorEvent<Any>) {
        LogUtil.log("TAG", "")
        if (networkErrorDialog == null) {
            networkErrorDialog = SingleBtnDialog(
                this,
                "네트워크 오류",
                "네트워크 연결 상태가 좋지 않습니다. 확인 후 다시 시도해 주세요.",
                btnCallback = {

                }
            )
        }
        networkErrorDialog!!.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: LogoutEvent) {
        LogUtil.log("TAG", "LogoutEvent")
        SingleBtnDialog(
            this,
            getString(R.string.logout_title),
            getString(R.string.auto_logout_desc)
        ) {
            AUTH_KEY = ""
            ID = ""
            findNavController(R.id.nav_main).navigate(R.id.loginGraph)
        }.show()
    }

    fun showLoadingDialog() {
        binding.loadingDialog.root.visibility = View.VISIBLE
    }

    fun dismissLoadingDialog() {
        binding.loadingDialog.root.visibility = View.GONE
    }

}