package ai.comake.petping.ui.home.walk

import ai.comake.petping.AirbridgeManager
import ai.comake.petping.R
import ai.comake.petping.data.vo.*
import ai.comake.petping.databinding.FragmentWalkBinding
import ai.comake.petping.google.database.room.walk.WalkRepository
import ai.comake.petping.observeEvent
import ai.comake.petping.ui.home.walk.adapter.MarkingDetailClusterRecyclerViewAdapter
import ai.comake.petping.ui.home.walk.adapter.SpaceItemDecoration
import ai.comake.petping.ui.home.walk.adapter.WalkGuideAdapter
import ai.comake.petping.ui.home.walk.dialog.WalkablePetDialog
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.EXTRA_LOCATION
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.EXTRA_WALK_STATUS
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.PAUSE
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.PLAY
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._audioGuideStatus
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._cameraPosition
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._cameraZoom
import ai.comake.petping.util.*
import android.Manifest
import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.CameraUpdate.REASON_DEVELOPER
import com.naver.maps.map.CameraUpdate.REASON_GESTURE
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolylineOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WalkFragment : Fragment(), OnMapReadyCallback {
    @Inject
    lateinit var walkRepository: WalkRepository

    private lateinit var binding: FragmentWalkBinding
    private val viewModel by viewModels<WalkViewModel>()
    private var mBound: Boolean = false
    private var mService: LocationUpdatesService? = null
    private lateinit var mContext: Context
    private lateinit var mNaverMap: NaverMap
    private lateinit var mMarkingDetailClusterListAdapter: MarkingDetailClusterRecyclerViewAdapter
    private lateinit var mAudioGuideListAdapter: WalkGuideAdapter
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mPetClusterView: View? = null
    private var mPetClusterSelectedView: View? = null
    private var mCameraChangeReason = REASON_DEVELOPER
    var mMarkingMarkers = mutableListOf<Marker>()
    var mClusterPOIs: List<MarkingPoi> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.log("TAG", "")
        requestPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtil.log("TAG", "")
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setUpObserver()
        setUpClickListener()
        setUpAdapter()
        setUpNaverMap()
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
                        _cameraPosition.value = lastLatLng
                        LogUtil.log("TAG", "${viewModel.cameraPosition.value}")
                    } else {
                        LogUtil.log(
                            TAG,
                            "Failed to get location."
                        )
                    }
                    asyncPOIs()
                    moveCamera()
                }
        } catch (unlikely: SecurityException) {
            asyncPOIs()
            moveCamera()
            LogUtil.log(
                TAG,
                "Lost location permission.$unlikely"
            )
        }
    }

    private fun moveCamera() {
        val cameraPosition = CameraPosition(
            viewModel.cameraPosition.value,
            viewModel.cameraZoom.value
        )
        mNaverMap.moveCamera(CameraUpdate.toCameraPosition(cameraPosition))

    }

    private fun setUpView() {
        activity?.window?.let { updateLightStatusBar(it) }

        //머커 뷰 초기화
        mPetClusterView =
            LayoutInflater.from(activity).inflate(R.layout.view_marking_cluster, null, false)
        mPetClusterSelectedView = LayoutInflater.from(activity)
            .inflate(R.layout.view_marking_cluster_selected, null, false)

        //오디오 가이드 상태 초기화
        _audioGuideStatus.value = AudioGuideStatus(
            false,
            "00:00",
            "00:00",
            "",
            "",
            0,
            0
        )
    }

    private fun setUpClickListener() {
        binding.walkTracker.startWalkButton.setSafeOnClickListener() {
            viewModel.asyncWalkablePet()
        }

        binding.walkTracker.walkControlView.walkPauseButton.setSafeOnClickListener {
            activity?.window?.let { updateDarkStatusBar(it) }
            mService?.pauseWalk(true)
            viewModel.pauseWalk(true)
        }

        binding.walkTracker.walkControlView.walkPlayButton.setSafeOnClickListener {
            activity?.window?.let { updateLightStatusBar(it) }
            mService?.pauseWalk(false)
            viewModel.pauseWalk(false)
        }

        binding.walkTracker.walkStopTrackingLocationImage.setSafeOnClickListener {
            mNaverMap.cancelTransitions()
            it.isSelected = true
            setUpLastLocation()
        }

        binding.walkTracker.walkControlView.walkStopButton.setSafeOnClickListener {
            mService?.startWalk(false)
            viewModel.startWalk(false)
        }

        binding.walkTracker.guideHeaderView.audioGuideButton.setSafeOnClickListener {
            binding.walkViewFlipper.displayedChild = 1
            viewModel.asyncWalkGuide(viewModel.walkGuidePageNo)
        }

        /**
         * 산책가이드 이벤트
         */
        binding.walkAudio.walkGuideBtnBack.setSafeOnClickListener {
            binding.walkViewFlipper.displayedChild = 0
        }

        binding.walkAudio.walkGuideTopButton.setOnClickListener {
            binding.walkAudio.walkGuideRecyclerView.scrollToPosition(0)
            val motion = binding.walkAudio.root as MotionLayout
            motion.progress = 0.0f
        }
    }

    private fun setUpObserver() {
        lifecycleScope.launch {
            viewModel.walkablePetList.collectLatest {
                openWalkablePetDialog(it as ArrayList<WalkablePet.Pets>)
            }
        }

        lifecycleScope.launch {
            viewModel.walkGuideListFlow.collectLatest { items ->
                LogUtil.log("TAG", "walkGuideListFlow " + items?.size)
                mAudioGuideListAdapter.submitList(items)
            }
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

        viewModel.isSucceedReadyForWalk.observeEvent(viewLifecycleOwner) {
            mService?.startWalk(true)
            viewModel.startWalk(true)
            LogUtil.log("TAG", "it $it")
        }

        viewModel.isFaiedReadyForWalk.observeEvent(viewLifecycleOwner) {
            LogUtil.log("TAG", "it $it")
        }

        viewModel.isSystemStopWalk.observeEvent(viewLifecycleOwner) { isComplete ->
            LogUtil.log("TAG", "isCompletedWalk $isComplete")
            if (isComplete) {
                viewModel.startWalk(false)
                viewLifecycleOwner.lifecycleScope.launch {
                    val walkData = walkRepository.selectAll()
                    LogUtil.log("TAG", "walkData $walkData")

                    activity?.findNavController(R.id.nav_main)
                        ?.navigate(R.id.action_home_to_walkrecordattach)
                }
            }
        }
    }

    private fun setUpAdapter() {
        viewModel.walkGuidePageNo = 1
        viewModel.hasMoreWalkGuideItem = false
        viewModel.walkGuideListItem = arrayListOf()

        mMarkingDetailClusterListAdapter = MarkingDetailClusterRecyclerViewAdapter(viewModel)
        binding.walkTracker.clusterDetailView.clusterRecyclerView.apply {
            addItemDecoration(
                SpaceItemDecoration(
                    requireActivity(),
                    R.drawable.shape_divider
                )
            )
            adapter = mMarkingDetailClusterListAdapter
        }

        mAudioGuideListAdapter = WalkGuideAdapter(
            this::startWalkWithAudioGuide,
            this::downloadAudioGuide,
            requireContext()
        )

        binding.walkAudio.walkGuideRecyclerView.adapter = mMarkingDetailClusterListAdapter

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
                        viewModel.asyncWalkGuide(viewModel.walkGuidePageNo)
                    }
                }
            })
            adapter = mAudioGuideListAdapter
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap
        mNaverMap.uiSettings.isZoomControlEnabled = false
        setUpLastLocation()
        setUpNaverMapUi()
        setUpNaverMapEvent()
    }

    private fun asyncPOIs() {
        unSelectPreviousPOI()
        viewModel.asyncPOIs()
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
            }
        }

        mNaverMap.addOnCameraIdleListener {
            _cameraZoom.value = mNaverMap.cameraPosition.zoom
            _cameraPosition.value = mNaverMap.cameraPosition.target

            if (mCameraChangeReason == REASON_GESTURE) {
                asyncPOIs()
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

    private fun displayPlacePOIs(places: List<PlacePoi.Places>) {
        places.forEach { item ->
            mMarkingMarkers.add(Marker().apply {
                position = LatLng(item.lat.decrypt(), item.lng.decrypt())
                icon = OverlayImage.fromResource(R.drawable.ic_poi_place_ground)
                tag = MarkerTag(item.id, WalkBottomUi.PLACE)
                setOnClickListener {
                    unSelectPreviousPOI()
                    selectCurrentPOI(this)
                    true
                }
                map = mNaverMap
            })
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
                asyncPlaceDeTail(clickTag.id)
            }
        }
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
        mMarkingDetailClusterListAdapter.submitList(getClusterPOIs(id))
        viewModel.updatePOIUi(WalkBottomUi.CLUSTER)
    }

    fun asyncPlaceDeTail(id: Int) {
        viewModel.updatePOIUi(WalkBottomUi.PLACE)
        viewModel.asyncPlaceDetail(id)
    }

    fun drawPath(walkPathList: ArrayList<WalkPath>) {
        val coords = ArrayList<LatLng>()
        for (i in 0 until walkPathList.size) {
            coords.add(LatLng(walkPathList[i].location))
        }

        PolylineOverlay().also {
            it.globalZIndex = 2
            it.width = 10
            it.color = ResourcesCompat.getColor(resources, R.color.color_ff4857, null)
            it.coords = coords
            it.map = mNaverMap
        }
    }

    private fun openWalkablePetDialog(items: ArrayList<WalkablePet.Pets>) {
        WalkablePetDialog(items) { petIds ->
            asyncWalkId(petIds)
            LogUtil.log("TAG", "petIds $petIds")
        }.show(
            childFragmentManager,
            null
        )
    }

    fun asyncWalkId(petIds: List<Int>) {
        val latitude = _cameraPosition.value.latitude
        val longitude = _cameraPosition.value.longitude
        viewModel.asyncWalkId(petIds, latitude, longitude)
    }

    fun startWalkWithAudioGuide(item: AudioGuideItem) {
        LogUtil.log("TAG", "")
        _audioGuideStatus.value.id = item.id
        _audioGuideStatus.value.audioFileId = item.audioFileId
        _audioGuideStatus.value.titleName = item.title
        _audioGuideStatus.value.speakerImageUrl = item.speakerThumbnailFileUrl
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

    private fun requestPermission() {
        when {
            hasPermission(mContext, LOCATION_AND_STORAGE) -> {
                LogUtil.log("TAG", "이미 권한 있음")
                createLocationClient()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                LogUtil.log("TAG", "한번 거절")
                requestPermissionLauncher.launch(LOCATION_AND_STORAGE)
            }
            else -> {
                requestPermissionLauncher.launch(LOCATION_AND_STORAGE)
            }
        }
    }

    private val requestPermissionLauncher =
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

    override fun onDestroy() {
        super.onDestroy()
        mService?.startWalk(false)
        viewModel.startWalk(false)
    }

    companion object {
        private val LOCATION_AND_STORAGE = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        const val TAG = "WalkFragment"
    }
}