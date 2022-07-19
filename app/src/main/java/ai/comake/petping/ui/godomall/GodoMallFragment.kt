package ai.comake.petping.ui.godomall

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentGodomallBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.backStack
import ai.comake.petping.util.setSafeOnClickListener
import ai.comake.petping.util.updateBlackStatusBar
import android.content.ActivityNotFoundException
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

/**
 * android-petping-2
 * Class: GodoMallActivity
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@AndroidEntryPoint
class GodoMallFragment : BaseFragment<FragmentGodomallBinding>(FragmentGodomallBinding::inflate) {

    private val viewModel by viewModels<GodoMallViewModel>()

    private val args: GodoMallFragmentArgs by navArgs()

    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val url = binding.webview.copyBackForwardList().currentItem?.url
            LogUtil.log("TAG", "handleOnBackPressed url : $url")
            when {
                binding.webview.canGoBack() -> binding.webview.goBack()
                else -> closePopup()
            }
        }
    }

    val selectFileChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (uploadMessage == null) return@registerForActivityResult
        uploadMessage?.onReceiveValue(
            WebChromeClient.FileChooserParams.parseResult(
                activityResult.resultCode,
                activityResult.data
            )
        )
        uploadMessage = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBlackStatusBar(requireActivity().window)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)

        with(viewModel) {

            shopLogin()

            loadingPage.observeEvent(viewLifecycleOwner) {
                binding.webview.load(args.config)
            }

            pingErrorPopup.observeEvent(viewLifecycleOwner) { errorBody ->
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.ping_point_error),
                    errorBody.message
                ) {}.show()
            }

            moveToLogin.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.logout_title),
                    getString(R.string.petping_logout_desc)
                ) {
                    requireActivity().findNavController(R.id.nav_main)
                        .navigate(R.id.action_godoMallFragment_to_loginGraph)
                }.show()
            }

            accessTokenExpire.observeEvent(viewLifecycleOwner) {
                SingleBtnDialog(
                    requireContext(),
                    getString(R.string.logout_title),
                    getString(R.string.petping_shop_logout_desc)
                ) {
                    requireActivity().backStack(R.id.nav_main)
                }.show()
            }

            showJsAlert.observeEvent(viewLifecycleOwner) { config ->
                SingleBtnDialog(
                    context = requireContext(),
                    title = "",
                    description = config.message!!,
                    cancelable = true,
                    btnCallback = { config.result?.confirm() }
                ).show()
            }

            showJsConfirm.observeEvent(viewLifecycleOwner) { config ->
                DoubleBtnDialog(
                    context = requireContext(),
                    title = "",
                    description = config.message!!,
                    cancelable = true,
                    cancelCallback = { config.result?.cancel() },
                    okCallback = { config.result?.confirm() }
                ).show()
            }

            showFileChooser.observeEvent(viewLifecycleOwner) { config ->
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }

                uploadMessage = config.callback

                val intent = config.params!!.createIntent()
                try {
                    selectFileChooser.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(
                        requireContext(),
                        "Cannot Open File Chooser",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.btnClose.setSafeOnClickListener { closePopup() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }

    private fun closePopup() {
        DoubleBtnDialog(
            requireContext(),
            getString(R.string.pet_ping_shop_close_title),
            getString(R.string.pet_ping_shop_close_message),
            true,
            okCallback = { requireActivity().backStack(R.id.nav_main) },
        ).show()
    }
}