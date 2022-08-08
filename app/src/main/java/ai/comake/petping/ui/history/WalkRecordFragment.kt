package ai.comake.petping.ui.history

import ai.comake.petping.R
import ai.comake.petping.databinding.FragmentWalkRecordBinding
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.base.BaseFragment
import ai.comake.petping.ui.home.walk.adapter.WalkRecordPictureAdapter
import ai.comake.petping.util.*
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.text.DecimalFormat

@AndroidEntryPoint
class WalkRecordFragment :
    BaseFragment<FragmentWalkRecordBinding>(FragmentWalkRecordBinding::inflate) {
    private val args by navArgs<WalkRecordFragmentArgs>()
    private lateinit var mContext: Context
    private lateinit var mWalkRecordPictureAdapter: WalkRecordPictureAdapter
    private val viewModel: WalkRecordViewModel by viewModels()
    private var walkId = 0

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            LogUtil.log("TAG", "handleOnBackPressed : ")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log("TAG", "")
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setUpObserver()
        setUpClickListener()
        setUpAdapter()
        setUpView()

        LogUtil.log("TAG", "args.walkFinish: ${args.walkFinish}")
    }

    override fun onResume() {
        super.onResume()
        activity?.let { AndroidBug5497Workaround(it).addListener() }
    }

    override fun onPause() {
        super.onPause()
        activity?.let { AndroidBug5497Workaround(it).removeListener() }
    }

    private fun setUpView() {
        val walkFinish = args.walkFinish

        walkId = walkFinish?.walk?.id ?: 0

        //산책 종료 종류에 따른 헤더 변
        when (walkFinish?.walk?.endState) {
            1 -> {
                changeUINormalStopWalk()
            }
            2, 8 -> {
                changeUIForceStopWalk()
            }
            3, 4, 5, 10 -> {
                changeUIAutoStopWalk()
            }
            else -> {
                changeUINormalStopWalk()
            }
        }

        //산책시간
        var walkHours = 0
        var walkMinutes = 0
        var walkSeconds = 0

        when {
            walkFinish?.walk?.realWalkTime?.size == 3 -> {
                walkHours = walkFinish.walk.realWalkTime[0]
                walkMinutes = walkFinish.walk.realWalkTime[1]
                walkSeconds = walkFinish.walk.realWalkTime[2]
            }
            walkFinish?.walk?.realWalkTime?.size == 2 -> {
                walkMinutes = walkFinish.walk.realWalkTime[0]
                walkSeconds = walkFinish.walk.realWalkTime[1]
            }
            walkFinish?.walk?.realWalkTime?.size == 1 -> {
                walkSeconds = walkFinish.walk.realWalkTime[0]
            }
        }

        val markingCount = walkFinish?.walk?.markingCount.toString()
        val walkDistance = walkFinish?.walk?.distanceString ?: "0km"
        if (walkHours > 0) {
            if (walkMinutes > 0) viewModel.markingTitle.value = ("${walkHours}시간 ${walkMinutes}분 동안\n${walkDistance} 산책을 하고\n${markingCount}개의 마킹을 남겼어요")
            else viewModel.markingTitle.value = ("${walkHours}시간 동안\n${walkDistance} 산책을 하고\n${markingCount}개의 마킹을 남겼어요")
        } else if (walkMinutes > 0) {
            viewModel.markingTitle.value = ("${walkMinutes}분 동안\n${walkDistance} 산책을 하고\n${markingCount}개의 마킹을 남겼어요")
        } else {
            viewModel.markingTitle.value = ("${walkSeconds}초 동안\n${walkDistance} 산책을 하고\n${markingCount}개의 마킹을 남겼어요")
        }

        val markingDetail = walkFinish?.pets?.joinToString { pet ->
            if(pet.markingCount > 0) {
                "${pet.name} ${pet.markingCount}개"
            } else {
                ""
            }
        }

        viewModel.markingDetail.value = markingDetail.toString()
        viewModel.isShowRewardView.value = walkFinish?.isMissionAchievement ?: false

        val decimalFormat = DecimalFormat("###,###")
        try {
            viewModel.rewardPoint.value = decimalFormat.format(walkFinish?.missionReward).toString()
        } catch (e: Exception) {
            //Do Nothing
        }

        walkFinish?.pictures?.let { list ->
            viewModel.pictureList.addAll(list)
        }

        mWalkRecordPictureAdapter.submitList(viewModel.pictureList.toMutableList())

//        activity?.window?.let{
//            keyboardVisibilityUtils = KeyboardVisibilityUtils(it,
//                onShowKeyboard = { keyboardHeight ->
//                    binding.svRoot.run {
//                        smoothScrollTo(scrollX, binding.memo.top)
//                    }
//                })
//
//            keyboardVisibilityUtils = KeyboardVisibilityUtils(it,
//                onHideKeyboard = {
//                    binding.outside.clearFocus()
//                })
//        }
    }

    private fun setUpObserver() {
        viewModel.isSucceedSave.observeEvent(viewLifecycleOwner) {
            requireActivity().findNavController(R.id.nav_main)
                .navigate(R.id.action_walkrecordattach_to_home)
        }
    }

    private fun setUpClickListener() {
        binding.saveButton.setSafeOnClickListener {
            activity?.let {
                val comment = viewModel.comment.value
                val imageList = viewModel.pictureList
                val reviewBody = MultipartBody.Part.createFormData("review", comment)
                val fileBody = ArrayList<MultipartBody.Part>()
                imageList.forEachIndexed { index, path ->
                    val byteArray = getResizeBitmap(it, path)
                    val filePart = byteArray?.let {
                        MultipartBody.Part.createFormData(
                            "file", "${index}.jpg", it.toRequestBody(
                                "image/jpg".toMediaTypeOrNull(), 0,
                                byteArray.size
                            )
                        )
                    }
                    if (filePart != null) {
                        fileBody.add(filePart)
                    }
                }

                viewModel.walkFinishRecord(walkId, reviewBody, fileBody)
            }
        }

        binding.pictureCloseButton.setOnClickListener{
            binding.pictureView.visibility = View.GONE
        }

        binding.rewardCloseButton.setOnClickListener{
            binding.rewardView.visibility = View.GONE
        }
    }

    private fun setUpAdapter() {
        mWalkRecordPictureAdapter =
            WalkRecordPictureAdapter(
                this::onPictureImageClick,
                this::onAddImageClick,
                this::onDeleteImageClick
            )
        binding.pictureRecyclerView.adapter = mWalkRecordPictureAdapter
    }

    private fun onPictureImageClick(url: String) {
        binding.pictureView.visibility = View.VISIBLE

        activity?.let {
            Glide.with(it).load(url).override(
                it.resources.displayMetrics.widthPixels,
                it.resources.displayMetrics.heightPixels
            ).optionalFitCenter().into(binding.pictureImage)
        }
    }

    private fun onAddImageClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            requestStoragePermission()
        } else {
            takePicture()
        }
    }

    private fun requestStoragePermission() {
        activity?.let {
            when {
                hasPermission(it, STORAGE_PERMISSION) -> {
                    LogUtil.log("TAG", "이미 권한 있음")
                    takePicture()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    LogUtil.log("TAG", "한번 거절")
                    requestStoragePermissionLauncher.launch(STORAGE_PERMISSION)
                }
                else -> {
                    requestStoragePermissionLauncher.launch(STORAGE_PERMISSION)
                }
            }
        }
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isGranted = permissions.entries.all {
                LogUtil.log("TAG", "it $it")
                it.value
            }
            if (isGranted) {
                takePicture()
                LogUtil.log("TAG", "모든 권한 있음 ")
            } else {
                LogUtil.log("TAG", "모든 권한 없음 ")
            }
        }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data = activityResult.data
            handleImage(data)
        }
    }

    private fun takePicture() {
        val intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        takePictureLauncher.launch(intent)
    }

    private fun handleImage(data: Intent?) {
        activity.let {
            val copyUri = getFileFromURI(data?.data!!, mContext)
            LogUtil.log("TAG", "data?.data!! ${copyUri}")
            if (copyUri.toString().isNotEmpty()) {
                viewModel.pictureList.add(copyUri.toString())
                mWalkRecordPictureAdapter.submitList(viewModel.pictureList.toMutableList())
            }
        }
    }

    private fun onDeleteImageClick(position: Int) {
        viewModel.pictureList.removeAt(position)
        mWalkRecordPictureAdapter.submitList(viewModel.pictureList.toMutableList())
    }

    private fun changeUIForceStopWalk() {
        activity?.window?.let { updateLightStatusBar(it) }
        binding.normalStopWalkHeader.visibility = View.GONE
        binding.forceStopHeader.visibility = View.VISIBLE
        binding.forceStopHeaderText2.text =
            "연관된 기능 중단으로 산책이 자동 종료되고\n종료 전 기록은 프로필에 안전하게 저장되었어요."
    }

    private fun changeUIAutoStopWalk() {
        activity?.window?.let { updateLightStatusBar(it) }
        binding.normalStopWalkHeader?.visibility = View.GONE
        binding.forceStopHeader.visibility = View.VISIBLE
        binding.forceStopHeaderText2.text = "산책 활동이 감지되지 않아 자동 종료되고\n종료 전 기록은 프로필에 안전하게 저장되었어요."
    }

    private fun changeUINormalStopWalk() {
        activity?.window?.let { updateDarkStatusBar(it) }
        binding.forceStopHeader.visibility = View.GONE
        binding.normalStopWalkHeader.visibility = View.VISIBLE
    }

}