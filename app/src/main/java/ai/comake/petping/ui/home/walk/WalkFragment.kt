package ai.comake.petping.ui.home.walk

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.Event
import ai.comake.petping.R
import ai.comake.petping.data.db.walk.Walk
import ai.comake.petping.data.vo.*
import ai.comake.petping.databinding.FragmentWalkBinding
import ai.comake.petping.google.database.room.walk.WalkDBRepository
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.common.dialog.DoubleBtnDialog
import ai.comake.petping.ui.common.dialog.SingleBtnDialog
import ai.comake.petping.ui.home.HomeFragmentDirections
import ai.comake.petping.ui.home.HomeShareViewModel
import ai.comake.petping.ui.home.walk.AudioGuidePlayer.Companion._audioGuideStatus
import ai.comake.petping.ui.home.walk.adapter.WalkClusterAdapter
import ai.comake.petping.ui.home.walk.adapter.WalkGuideAdapter
import ai.comake.petping.ui.home.walk.dialog.MarkingBottomSheetDialog
import ai.comake.petping.ui.home.walk.dialog.WalkEndBottomSheetDialog
import ai.comake.petping.ui.home.walk.dialog.WalkablePetDialog
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.EXTRA_LOCATION
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.EXTRA_WALK_STATUS
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.PAUSE
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.PLAY
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._cameraPosition
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._cameraZoom
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._lastLocation
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._myMarkingList
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._picturePaths
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.localWalkData
import ai.comake.petping.util.*
import android.Manifest
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.naver.maps.geometry.Coord
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.CameraUpdate.REASON_DEVELOPER
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.overlay.PolylineOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class WalkFragment : Fragment(), OnMapReadyCallback {
    @Inject
    lateinit var walkDBRepository: WalkDBRepository

    private lateinit var binding: FragmentWalkBinding
    private val viewModel by viewModels<WalkViewModel>()
    private val homeShareViewModel: HomeShareViewModel by activityViewModels()
    private var mBound: Boolean = false
    private var mService: LocationUpdatesService? = null
    private lateinit var mContext: Context
    private lateinit var mNaverMap: NaverMap
    private var mWalkClusterAdapter: WalkClusterAdapter? = null
    private lateinit var mAudioGuideListAdapter: WalkGuideAdapter
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mPetClusterView: View? = null
    private var mPetClusterSelectedView: View? = null
    private var mCameraChangeReason = REASON_DEVELOPER
    var mMarkingMarkers = mutableListOf<Marker>()
    var mClusterPOIs: List<MarkingPoi> = emptyList()
    var walkData = listOf<Walk>()

    var walkablePetDialog: WalkablePetDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.log("TAG", "")
        requestLocationPermission()
        //오디오 가이드 상태 초기화
        initialAudioGuideStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LogUtil.log("TAG", "${this::binding.isInitialized}")
        if (!this::binding.isInitialized) {
            binding = FragmentWalkBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.log("TAG", "")
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.event = this
        setUpNaverMap()
        setUpObserver()
        setUpClickListener()
        setUpAdapter()
        setUpView()
    }

    private fun setUpNaverMap() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    private fun setUpLastLocation() {
        LogUtil.log("TAG", "")
        try {
            mFusedLocationClient?.lastLocation
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val lastLatLng = LatLng(task.result)
                        val lastLocation = Location(LocationManager.GPS_PROVIDER).apply {
                            latitude = task.result.latitude
                            longitude = task.result.longitude
                        }
                        _lastLocation = lastLocation
                        _cameraPosition.value = lastLatLng

                        val locationOverlay = mNaverMap.locationOverlay
                        val latLng = LatLng(_lastLocation.latitude, _lastLocation.longitude)
                        locationOverlay.position = latLng
                        locationOverlay.icon = OverlayImage.fromResource(R.drawable.here_moving)
                        locationOverlay.isVisible = true
                    } else {
                        LogUtil.log(
                            TAG,
                            "Failed to get location."
                        )
                    }
                    asyncAllPOIs()
                    setUpCameraPosition()
                }
        } catch (unlikely: SecurityException) {
            asyncAllPOIs()
            setUpCameraPosition()
            LogUtil.log(
                TAG,
                "Lost location permission.$unlikely"
            )
        }
    }

    private fun setUpCameraPosition() {
        val cameraPosition = CameraPosition(
            viewModel.cameraPosition.value,
            viewModel.cameraZoom.value
        )
        mNaverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))
    }

    private fun setUpView() {
        activity?.window?.let { window ->
            updateLightStatusBar(window)
//            paddingStatusBarHeight(window.context,window.decorView)
        }

        //머커 뷰 초기화
        mPetClusterView =
            LayoutInflater.from(activity).inflate(R.layout.view_marking_cluster, null, false)
        mPetClusterSelectedView = LayoutInflater.from(activity)
            .inflate(R.layout.view_marking_cluster_selected, null, false)
    }

    private fun initialAudioGuideStatus() {
        _audioGuideStatus.value = AudioGuideStatus()
    }

    private fun setUpClickListener() {
        binding.walkTracker.startWalkButton.setSafeOnClickListener() {
            viewModel.asyncWalkablePet()
        }

        binding.walkTracker.walkControlView.walkPauseButton.setSafeOnClickListener {
            activity?.window?.let { updateDarkStatusBar(it) }
            mService?.pauseWalk(true)
            viewModel.pauseWalk(true)

            mService?.pauseAudioGuide(true)
        }

        binding.walkTracker.walkControlView.walkPlayButton.setSafeOnClickListener {
            activity?.window?.let { updateLightStatusBar(it) }
            mService?.pauseWalk(false)
            viewModel.pauseWalk(false)

            mService?.pauseAudioGuide(false)
        }

        binding.walkTracker.walkStopTrackingLocationImage.setSafeOnClickListener {
            mNaverMap.cancelTransitions()
            it.isSelected = true
            setUpLastLocation()
        }

        binding.walkTracker.bottomFloatingView.walkStartTrackingLocationButton.setSafeOnClickListener {
            mNaverMap.cancelTransitions()
            it.isSelected = true
            setUpLastLocation()
        }

        binding.walkTracker.walkControlView.walkStopButton.setSafeOnClickListener {
            activity?.let {
                WalkEndBottomSheetDialog {
                    mService?.startWalk(false)
                    viewModel.startWalk(false)
                }.showAllowingStateLoss(childFragmentManager)
            }
        }

        binding.walkTracker.guideHeaderView.audioGuideButton.setSafeOnClickListener {
            binding.walkViewFlipper.displayedChild = 1
            homeShareViewModel.isVisibleBottomNavigation.value = Event(false)
            viewModel.asyncWalkGuide(viewModel.walkGuidePageNo)
        }

        binding.walkAudio.walkGuideBtnBack.setSafeOnClickListener {
            hideAudioGuide()
        }

        binding.walkAudio.walkGuideTopButton.setOnClickListener {
            binding.walkAudio.walkGuideRecyclerView.scrollToPosition(0)
            val motion = binding.walkAudio.root as MotionLayout
            motion.progress = 0.0f
        }

        binding.walkTracker.walkControlView.walkPhotoButton.setSafeOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                requestStoragePermission()
            } else {
                takePicture()
            }
        }

        binding.walkTracker.walkControlView.walkMarkingButton.setSafeOnClickListener {
            activity?.let {
                MarkingBottomSheetDialog(
                    viewModel.walkAblePetList,
                    this::onMarkingRegisterClick
                ).show(
                    it.supportFragmentManager,
                    "MarkingBottomSheetDialog"
                )
            }
        }
    }

    fun onMarkingRegisterClick(petId: Int, type: Int) {
        val myMarkingPoi =
            MyMarkingPoi(
                petId,
                type,
                _lastLocation.latitude.encrypt(),
                _lastLocation.longitude.encrypt()
            )

        viewModel.asyncRegisterMarking(localWalkData.walkId, myMarkingPoi)

        _myMarkingList.value.add(myMarkingPoi)
        displayMyMarkingPOI(_myMarkingList.value)
    }

    fun onClickNavWalkHistoryDetail(petId: Int) {
        val config = PetProfileConfig(
            petId = petId,
            viewMode = "others"
        )

        requireActivity().findNavController(R.id.nav_main)
            .navigate(HomeFragmentDirections.actionHomeScreenToDogProfileFragment(config))
    }

    private fun setUpObserver() {
        viewModel.walkablePetList.observeEvent(viewLifecycleOwner) {
            openWalkablePetDialog(it as ArrayList<WalkablePet.Pets>)
        }
//
//        lifecycleScope.launch {
//            viewModel.walkGuideListFlow.collectLatest { items ->
//                mAudioGuideListAdapter.submitList(items)
//            }
//        }

        viewModel.walkGuideItem.observeEvent(viewLifecycleOwner) { items ->
            LogUtil.log("TAG", "items: $items")
            mAudioGuideListAdapter.submitList(items)
        }

        viewModel.clearMarker.observeEvent(viewLifecycleOwner) {
            clearAllMarker()
        }

        viewModel.markingPOIs.observeEvent(viewLifecycleOwner) {
            displayMarkingPOIs(it)
            mClusterPOIs = it
        }

        viewModel.placePOIs.observeEvent(viewLifecycleOwner) {
            displayPlacePOIs(it)
        }

        viewModel.isSucceedReadyForWalk.observeEvent(viewLifecycleOwner) { walkstart ->
            walkablePetDialog?.dismissWalkablePetDialog()

            localWalkData =
                Walk(
                    walkId = walkstart.walk.id,
                    startLat = _lastLocation.latitude,
                    startLng = _lastLocation.longitude,
                    petIds = walkstart.walk.petIds
                )

            if (binding.walkAudio.root.isVisible && _audioGuideStatus.value!!.audioFileId != 0) {
                hideAudioGuide()
                mService?.setUpAudioPlayer()
                viewModel.requestAudioGuideLog(
                    localWalkData.walkId.toString(),
                    _audioGuideStatus.value!!.id.toString()
                )
            } else {
                initialAudioGuideStatus()
                binding.walkTracker.guideHeaderView.root.visibility = View.GONE
            }

            mService?.startWalk(true)
            viewModel.startWalk(true)
        }

        viewModel.isFailedReadyForWalk.observeEvent(viewLifecycleOwner) { errorResponse ->
            if (errorResponse != null) {
                if (errorResponse.code == "C4080" || errorResponse.code == "C4090") {
                    SingleBtnDialog(
                        mContext,
                        "산책할 수 없습니다.",
                        errorResponse.message
                    ) {
                    }.show()
                } else if (errorResponse.code == "C4081") {
                    SingleBtnDialog(mContext, "기존 산책이 종료되지 않음", errorResponse.message) {
                    }.show()
                }
            }
        }

        viewModel.isStopWalkService.observeEvent(viewLifecycleOwner) { isComplete ->
            LogUtil.log("TAG", "isCompletedWalk $isComplete")
            if (isComplete) {
                viewModel.startWalk(false)
                requestWalkFinish()
//                    activity?.findNavController(R.id.nav_main)
//                        ?.navigate(R.id.action_home_to_walkrecordattach)
            }
        }

        viewModel.isSucceedWalkFinish.observeEvent(viewLifecycleOwner) { walkfinish ->
            LogUtil.log("TAG", "walkfinish: $walkfinish")
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                LogUtil.log("TAG", "walkDBRepository: ${walkDBRepository.selectAll()}")
                withContext(Dispatchers.Main) {
                    walkfinish.pictures = walkData[0].pictures
                    requireActivity().findNavController(R.id.nav_main).navigate(
                        HomeFragmentDirections.actionHomeToWalkrecordattach()
                            .setWalkFinish(walkfinish)
                    )
                }
            }
        }

        viewModel.readyToGuideProgress.observe(viewLifecycleOwner) { position ->
            if (position >= 0) {
                mAudioGuideListAdapter.readyDownloadProgress(position)
            }
        }

        viewModel.startAudioGuide.observe(viewLifecycleOwner) {
            LogUtil.log("TAG", "it: $it")
            if (it) {
                startAudioGuide()
            }
        }

        viewModel.endAudioGuide.observe(viewLifecycleOwner) {
            LogUtil.log("TAG", "it: $it")
            if (it) {
                endAudioGuide()
            }
        }

        viewModel.isPauseAudioGuide.observe(viewLifecycleOwner) {
            LogUtil.log("TAG", "it: $it")
            if (_audioGuideStatus.value?.isEndAudio == false) {
                isPauseAudioGuide(it)
            }
        }

        viewModel.downloadNetworkError.observe(viewLifecycleOwner) { vo ->
            if (vo.code == 1) {
                activity?.let {
                    DoubleBtnDialog(
                        it,
                        "네트워크 오류",
                        "네트워크 연결 상태가 좋지 않습니다. 확인 후 다시 시도해 주세요.",
                        false,
                        "앱 종료",
                        "재시도",
                        {
                            it.finish()
                        },
                        {
                            viewModel.downloadFile(it, vo.url, vo.fileName, vo.position)
                        }).show()
                }
            }
        }

        viewModel.downloadProgress.asLiveData().observe(viewLifecycleOwner) { progressVo ->
            lifecycleScope.launch(Dispatchers.Main) {
                if (binding.walkViewFlipper.displayedChild == 1) {
                    if (progressVo.percent > 0) {
                        mAudioGuideListAdapter.updateDownloadProgress(
                            progressVo.percent,
                            progressVo.position
                        )
                    }

                    if (progressVo.percent == 100) {
                        mAudioGuideListAdapter.updateDownloadComplete(
                            progressVo.position
                        )
                    }
                }
            }
        }
    }

    fun hideAudioGuide() {
        binding.walkViewFlipper.displayedChild = 0
        homeShareViewModel.isVisibleBottomNavigation.value = Event(true)
//        mAudioGuideListAdapter = WalkGuideAdapter(
//            this::startWalkWithAudioGuide,
//            this::downloadAudioGuide,
//            requireContext()
//        )
    }

    private fun requestWalkFinish() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            walkData = walkDBRepository.selectAll()
            LogUtil.log("TAG", "walkData $walkData")
        }.invokeOnCompletion { throwable ->
            if (walkData.isNotEmpty()) {
                when (throwable) {
                    is CancellationException -> {}
                    else -> {
                        val id = walkData[0].walkId
                        val body =
                            WalkFinishRequest(
                                walkData[0].distance,
                                walkData[0].time,
                                walkData[0].endState,
                                walkData[0].path,
                                walkData[0].walkEndDatetimeMilli
                            )
                        viewModel.asyncWalkFinish(SAPA_KEY, id, body)
                    }
                }
            }
        }
    }

    private fun setUpAdapter() {
        viewModel.walkGuidePageNo = 1
        viewModel.walkGuideTotalCount = 0
        viewModel.hasMoreWalkGuideItem = false
        viewModel.walkGuideListItem = arrayListOf()

        if (mWalkClusterAdapter == null) {
            mWalkClusterAdapter =
                WalkClusterAdapter(viewModel, this::onClickNavWalkHistoryDetail)
            binding.walkTracker.clusterDetailView.clusterRecyclerView.apply {
                adapter = mWalkClusterAdapter
            }
        }

        mAudioGuideListAdapter = WalkGuideAdapter(
            this::startWalkWithAudioGuide,
            this::downloadAudioGuide,
            requireContext()
        )

        binding.walkAudio.walkGuideRecyclerView.adapter = mAudioGuideListAdapter

        binding.walkAudio.walkGuideRecyclerView.apply {
            layoutManager = LinearLayoutManager(mContext)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    (layoutManager as LinearLayoutManager).apply {
                        when (findFirstCompletelyVisibleItemPosition()) {
                            0 -> binding.walkAudio.walkGuideTopButton.visibility =
                                View.GONE
                            else -> {
                                binding.walkAudio.walkGuideTopButton.visibility =
                                    View.VISIBLE
                            }
                        }
                    }

                    if (canScrollVertically(1).not() && viewModel.hasMoreWalkGuideItem) {
                        viewModel.asyncWalkGuide(viewModel.walkGuidePageNo, true)
                    }
                }
            })
            adapter = mAudioGuideListAdapter
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap
        mNaverMap.uiSettings.isZoomControlEnabled = false
        mNaverMap.maxZoom = 20.0
        mNaverMap.minZoom = 13.0
        setUpLastLocation()
        setUpNaverMapUi()
        setUpNaverMapEvent()
    }

    private fun asyncAllPOIs() {
        unSelectPreviousPOI()
        viewModel.asyncAllPOIs()
    }

    private fun setUpNaverMapUi() {


        if (viewModel.walkPathList.value.size > 1) {
            drawPath(viewModel.walkPathList.value)
        }
    }

    private fun setUpNaverMapEvent() {
        mNaverMap.setOnMapClickListener { _, _ ->
            viewModel.cancelPOI()
            unSelectPreviousPOI()
        }

        mNaverMap.addOnCameraChangeListener { reason, _ ->
            mCameraChangeReason = reason
            if (mCameraChangeReason == REASON_GESTURE) {
                binding.walkTracker.walkStopTrackingLocationImage.isSelected = false
                binding.walkTracker.bottomFloatingView.walkStartTrackingLocationButton.isSelected =
                    false
            }
        }

        mNaverMap.addOnCameraIdleListener {
            _cameraZoom.value = mNaverMap.cameraPosition.zoom
            _cameraPosition.value = mNaverMap.cameraPosition.target

            if (mCameraChangeReason == REASON_GESTURE) {
                asyncAllPOIs()
            }
        }
    }

    private fun getClusterMarkerImage(count: Int, isSelected: Boolean): Bitmap {
        val view = if (isSelected) {
            mPetClusterSelectedView
        } else {
            mPetClusterView
        }
        view?.findViewById<TextView>(R.id.markingCount)?.text = count.toString()
        return getBitmapFromView(view)!!
    }

    private fun displayMarkingPOIs(markings: List<MarkingPoi>) {
        markings.forEach { item ->
            mMarkingMarkers.add(Marker().apply {
                if (item.clusteredCount == 1) {
                    globalZIndex = 2
                    position = LatLng(item.lat.decrypt(), item.lng.decrypt())
                    icon = OverlayImage.fromResource(R.drawable.ic_poi_marking)
                    tag = MarkerTag(item.pois[0].id, WalkBottomUi.MARKING)
                    setOnClickListener {
                        unSelectPreviousPOI()
                        selectCurrentPOI(this)
                        true
                    }
                } else {
                    position = LatLng(item.lat.decrypt(), item.lng.decrypt())
                    globalZIndex = 2
                    val count = item.clusteredCount
                    icon = OverlayImage.fromBitmap(getClusterMarkerImage(count, false))
                    tag = MarkerTag(item.clusteredId, WalkBottomUi.CLUSTER, count)
                    setOnClickListener {
                        unSelectPreviousPOI()
                        selectCurrentPOI(this)
                        true
                    }
                }

                map = mNaverMap
            })
        }
    }

    private fun displayPlacePOIs(places: List<PlacePoi>) {
        places.forEach { item ->
            mMarkingMarkers.add(Marker().apply {
                globalZIndex = 2
                position = LatLng(item.lat.decrypt(), item.lng.decrypt())
                icon = OverlayImage.fromResource(R.drawable.ic_poi_place_ground)
                tag = MarkerTag(item.id, WalkBottomUi.PLACE, 1, item)
                setOnClickListener {
                    unSelectPreviousPOI()
                    selectCurrentPOI(this)
                    true
                }
                map = mNaverMap
            })
        }
    }

    private fun displayMyMarkingPOI(myMarkings: List<MyMarkingPoi>) {
        LogUtil.log("TAG", "item: $myMarkings")
        myMarkings.forEach { item ->
            Marker().apply {
                globalZIndex = 4
                position = LatLng(item.lat.decrypt(), item.lng.decrypt())
                icon = OverlayImage.fromResource(R.drawable.ic_poi_my_marking)
                map = mNaverMap
            }
        }
    }

    private fun getClusterPOIs(id: Int): List<MarkingPoi.Pois> {
        return mClusterPOIs.filter {
            it.clusteredId == id
        }.map {
            it.pois
        }.flatten()
    }

    private fun selectCurrentPOI(marker: Marker) {
        val clickTag = marker.tag as MarkerTag
        viewModel.selectedMarkerId = clickTag.id

        when (clickTag.type) {
            WalkBottomUi.MARKING -> {
                marker.icon = OverlayImage.fromResource(R.drawable.ic_poi_marking_select)
                asyncMarkingDetail(clickTag.id)
            }
            WalkBottomUi.CLUSTER -> {
                marker.icon =
                    OverlayImage.fromBitmap(getClusterMarkerImage(clickTag.count, true))
                asyncMarkingClusterDetail(clickTag.id)
            }
            WalkBottomUi.PLACE -> {
                marker.icon =
                    OverlayImage.fromResource(R.drawable.ic_poi_place_ground_select)
                viewModel.updatePOIUi(WalkBottomUi.PLACE)
                viewModel._placeDetail.value = clickTag.placePoi
            }
        }

        marker.globalZIndex = 5
        marker.map = mNaverMap
    }

    private fun unSelectPreviousPOI() {
        mMarkingMarkers.forEach { marker ->
            val clickTag = marker.tag as MarkerTag
            if (clickTag.id == viewModel.selectedMarkerId) {
                when (clickTag.type) {
                    WalkBottomUi.MARKING -> {
                        marker.icon = OverlayImage.fromResource(R.drawable.ic_poi_marking)
                    }
                    WalkBottomUi.CLUSTER -> {
                        marker.icon =
                            OverlayImage.fromBitmap(getClusterMarkerImage(clickTag.count, false))
                    }
                    WalkBottomUi.PLACE -> {
                        marker.icon =
                            OverlayImage.fromResource(R.drawable.ic_poi_place_ground)
                    }
                }

                marker.globalZIndex = 2
                marker.map = mNaverMap
            }
        }
    }

    private fun clearAllMarker() {
        mMarkingMarkers.map {
            it.map = null
        }
        mMarkingMarkers.clear()
    }

    fun asyncMarkingDetail(id: Int) {
        viewModel.updatePOIUi(WalkBottomUi.MARKING)
        viewModel.asyncMarkingDetail(id)
    }

    fun asyncMarkingClusterDetail(id: Int) {
        mWalkClusterAdapter?.apply {
            stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            submitList(getClusterPOIs(id))
        }
        viewModel.updatePOIUi(WalkBottomUi.CLUSTER)
    }

    fun drawPath(walkPathList: ArrayList<WalkPath>) {
        val coords = ArrayList<LatLng>()
        for (i in 0 until walkPathList.size) {
            coords.add(LatLng(walkPathList[i].location))
        }

        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.position = coords.last()

        PolylineOverlay().also {
            it.globalZIndex = 3
            it.width = 10
            it.color = ResourcesCompat.getColor(resources, R.color.color_ff4857, null)
            it.coords = coords
            it.map = mNaverMap
        }
    }

    private fun openWalkablePetDialog(items: ArrayList<WalkablePet.Pets>) {
        walkablePetDialog = WalkablePetDialog(items) { item ->
            viewModel.walkAblePetList = item as ArrayList<WalkablePet.Pets>
            val petIds = item.map {
                it.id
            }
            asyncWalkId(petIds)
        }
        walkablePetDialog?.showAllowingStateLoss(childFragmentManager)
    }

    fun asyncWalkId(petIds: List<Int>) {
        val latitude = _cameraPosition.value.latitude
        val longitude = _cameraPosition.value.longitude
        viewModel.asyncWalkId(petIds, latitude, longitude)
    }

    fun startWalkWithAudioGuide(item: AudioGuideItem) {
        LogUtil.log("TAG", "item: $item")
        _audioGuideStatus.value!!.id = item.id
        _audioGuideStatus.value!!.audioFileId = item.audioFileId
        _audioGuideStatus.value!!.setTitleName(item.title)
        _audioGuideStatus.value!!.setSpeakerImageUrl(item.speakerThumbnailFileUrl)
        viewModel.asyncWalkablePet()
    }

    fun downloadAudioGuide(url: String, fileName: String, position: Int) {
        LogUtil.log("TAG", "")
        viewModel.downloadFile(requireContext(), url, fileName, position)

        AirbridgeManager.trackEvent(
            "audio_download_button",
            "downloadButtonTapped",
            label = "walkGuideTitle"
        )
    }

    private fun requestLocationPermission() {
        when {
            hasPermission(mContext, LOCATION_PERMISSION) -> {
                LogUtil.log("TAG", "이미 권한 있음")
                createLocationClient()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                LogUtil.log("TAG", "한번 거절")
                requestLocationPermissionLauncher.launch(LOCATION_PERMISSION)
            }
            else -> {
                requestLocationPermissionLauncher.launch(LOCATION_PERMISSION)
            }
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isGranted = permissions.entries.all {
                it.value
            }
            if (isGranted) {
                LogUtil.log("TAG", "모든 권한 있음 ")
                createLocationClient()
            } else {
                LogUtil.log("TAG", "모든 권한 없음 ")
            }
        }

    private fun requestStoragePermission() {
        when {
            hasPermission(mContext, STORAGE_PERMISSION) -> {
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

    private val requestStoragePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isGranted = permissions.entries.all {
                LogUtil.log("TAG", "it $it")
                it.value
            }
            if (isGranted) {
                LogUtil.log("TAG", "모든 권한 있음 ")
                takePicture()
            } else {
                LogUtil.log("TAG", "모든 권한 없음 ")
            }
        }


    private fun createLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
    }

    /**
     * Receiver for broadcasts sent by [LocationUpdatesService].
     */
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when {
                intent.hasExtra(EXTRA_LOCATION) -> {
                    val coords =
                        intent.getSerializableExtra(EXTRA_LOCATION) as ArrayList<WalkPath>
                    if (coords.size > 1) {
                        drawPath(coords)
                    }
                }
                intent.hasExtra(EXTRA_WALK_STATUS) -> {
                    val walkStatus = intent.getIntExtra(EXTRA_WALK_STATUS, 0)
                    when (walkStatus) {
                        PAUSE -> {
                            activity?.window?.let { updateDarkStatusBar(it) }
                            viewModel.pauseWalk(true)
                        }
                        PLAY -> {
                            activity?.window?.let { updateLightStatusBar(it) }
                            viewModel.pauseWalk(false)
                        }
                    }
                }
            }
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    /**
     * 사진 찍기
     */
    fun takePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(mContext.packageManager)?.also {
                LogUtil.log("TAG", "")
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }

                if (photoFile != null) {
                    viewModel.takePhotoPath = photoFile.path
                    openCamera(takePictureIntent, photoFile)
                } else {
                    SingleBtnDialog(
                        mContext,
                        "에러",
                        "파일을 저장하는데 실패했습니다."
                    ) {}.show()
                }

            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val directoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val filePath = File(directoryPath.path + "/Petping")
        if (!filePath.exists())
            filePath.mkdir()
        return File("${filePath}", " PETPING_${timeStamp}.jpg")
    }

    private fun openCamera(intent: Intent, file: File) {
        val photoURI: Uri = FileProvider.getUriForFile(
            mContext, mContext.packageName + ".provider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        requestTakePhotoLauncher.launch(intent)
    }

    private val requestTakePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            LogUtil.log("TAG", "resultCode: $")
            _picturePaths.value.add(viewModel.takePhotoPath)
            viewModel.pictureCount.value = _picturePaths.value.size
            if (_picturePaths.value.size >= 5) {
                binding.walkTracker.walkControlView.walkPhotoButton.isClickable = false
                binding.walkTracker.walkControlView.walkPhotoButton.isEnabled = false
                binding.walkTracker.walkControlView.walkPhotoButton.elevation =
                    5.dpToPixels(requireContext())
                binding.walkTracker.walkControlView.walkPhotoButton.setColorFilter(
                    Color.parseColor(
                        "#dddddd"
                    )
                )
                binding.walkTracker.walkControlView.walkPhotoCount.background =
                    mContext.getDrawable(R.drawable.shape_badge_gray)
            }
        } else {
            LogUtil.log("TAG", "resultCode: else$")
            if (viewModel.picturePaths.value.size > 0) {
                val file = File(viewModel.picturePaths.value[0])
                file.deleteOnExit()
            }
        }
    }

//    private fun displayWalkGuide(list: ArrayList<AudioGuideItem>) {
//        mAudioGuideListAdapter.submitList(list)
//    }
//
//    private fun loadWalkGuide(pageNo: Int) {
//        LogUtil.log("TAG", "")
//        viewModel.asyncWalkGuide(pageNo)
//    }

    fun endAudioGuide() {
        try {
            binding.walkTracker.guideHeaderView.layoutRipplepulse.stopRippleAnimation()
            binding.walkTracker.guideHeaderView.audioGuideTitle.visibility = View.GONE
            binding.walkTracker.guideHeaderView.audioGuideTitleEnd.visibility = View.VISIBLE
            binding.walkTracker.guideHeaderView.audioRunningTime.visibility = View.GONE
            binding.walkTracker.guideHeaderView.audioTimeDivider.visibility = View.GONE
            binding.walkTracker.guideHeaderView.progressBottomLine.setBackgroundColor(
                Color.parseColor(
                    "#14000000"
                )
            )
            binding.walkTracker.guideHeaderView.speakerRoundBg.setBackgroundResource(R.drawable.circle_border_gray)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun startAudioGuide() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                binding.walkTracker.guideHeaderView.layoutRipplepulse.startRippleAnimation()
                binding.walkTracker.guideHeaderView.progressBottomLine.setBackgroundColor(
                    Color.parseColor(
                        "#FF4857"
                    )
                )
                binding.walkTracker.guideHeaderView.speakerRoundBg.setBackgroundResource(R.drawable.circle_border_red)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun isPauseAudioGuide(isPause: Boolean) {
        LogUtil.log("TAG", "")
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                if (isPause) {
                    binding.walkTracker.guideHeaderView.layoutRipplepulse.stopRippleAnimation()
                } else {
                    binding.walkTracker.guideHeaderView.layoutRipplepulse.startRippleAnimation()
                }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(mContext, LocationUpdatesService::class.java).also { intent ->
            mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(mContext).registerReceiver(
            mReceiver,
            IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            mContext.unbindService(serviceConnection)
            mBound = false
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtil.log("TAG", " $hidden")
    }

    override fun onDestroy() {
        super.onDestroy()
        mService?.startWalk(false)
        viewModel.startWalk(false)
    }

    companion object {
        private val LOCATION_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        private val STORAGE_PERMISSION = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private val REQUEST_TAKE_PHOTO = 11
        const val TAG = "WalkFragment"
    }
}