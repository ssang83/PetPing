package ai.comake.petping.ui.home.walk

import ai.comake.petping.AppConstants
import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.WalkRepository
import ai.comake.petping.data.vo.*
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._audioGuideStatus
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._cameraPosition
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._cameraZoom
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._isPauseWalk
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._isStartWalk
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._isSystemStopWalk
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._isWithAudioGuide
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._walkDistanceKm
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._walkPathList
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._walkTimeSeconds
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.downloadToFileWithProgress
import ai.comake.petping.util.encrypt
import ai.comake.petping.util.getAudioGuideList
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

@HiltViewModel
class WalkViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var walkRepository: WalkRepository

    private val mContext by lazy { getApplication<Application>().applicationContext }

    val isStartWalk = _isStartWalk
    val isPauseWalk = _isPauseWalk
    val isSystemStopWalk: LiveData<Event<Boolean>>
        get() = _isSystemStopWalk

    val walkDistanceKm = _walkDistanceKm
    val walkTimeSeconds = _walkTimeSeconds
    val cameraPosition = _cameraPosition
    val cameraZoom = _cameraZoom
    val walkPathList = _walkPathList

    val isAudioGuideHeader = MutableStateFlow(true)
    val isAudioGuideBottom = MutableStateFlow(false)

    val audioGuideStatus = _audioGuideStatus

    private val _isSucceedReadyForWalk = MutableLiveData<Event<WalkStart>>()
    val isSucceedReadyForWalk: LiveData<Event<WalkStart>>
        get() = _isSucceedReadyForWalk

    private val _isFaiedReadyForWalk = MutableLiveData<Event<ErrorResponse>>()
    val isFaiedReadyForWalk: LiveData<Event<ErrorResponse>>
        get() = _isFaiedReadyForWalk

    private val _clearMarker = MutableLiveData<Event<Boolean>>()
    val clearMarker: LiveData<Event<Boolean>>
        get() = _clearMarker

    private val _markingPOIs = MutableLiveData<Event<List<MarkingPoi>>>()
    val markingPOIs: LiveData<Event<List<MarkingPoi>>>
        get() = _markingPOIs

    private val _placePOIs = MutableLiveData<Event<List<PlacePoi.Places>>>()
    val placePOIs: LiveData<Event<List<PlacePoi.Places>>>
        get() = _placePOIs

    private val _markingDetail = MutableStateFlow<MarkingDetail?>(null)
    val markingDetail = _markingDetail.asStateFlow()

    private val _placeDetail = MutableStateFlow<PlaceDetail?>(null)
    val placeDetail = _placeDetail.asStateFlow()

    private val _walkablePetList = MutableSharedFlow<List<WalkablePet.Pets>?>()
    val walkablePetList = _walkablePetList.asSharedFlow()

    private val _walkBottomUi = MutableStateFlow(WalkBottomUi.NONE)
    val walkBottomUi = _walkBottomUi.asStateFlow()

    private val _readyToGuideProgress = MutableLiveData<Event<Int>>()
    val readyToGuideProgress: LiveData<Event<Int>>
        get() = _readyToGuideProgress

    private val _downloadProgress = MutableStateFlow(DownLoadProgress(0, 0))
    val downloadProgress = _downloadProgress.asStateFlow()

    private val _downloadComplete = MutableLiveData<Event<File>>()
    val downloadComplete: LiveData<Event<File>>
        get() = _downloadComplete

    var selectedMarkerId: Int = 0

    var walkGuideListItem = ArrayList<AudioGuideItem>()

    private val _walkGuideListFlow = MutableSharedFlow<List<AudioGuideItem>?>()
    val walkGuideListFlow = _walkGuideListFlow.asSharedFlow()

    var walkGuidePageNo: Int = 1
    var hasMoreWalkGuideItem: Boolean = false

    fun startWalk(isStart: Boolean) {
        _isStartWalk.value = isStart
    }

    fun pauseWalk(isPause: Boolean) {
        _isPauseWalk.value = isPause
    }

    fun onWalkablePetClick() {
        LogUtil.log("TAG", "")
    }

    fun cancelPOI() {
        updatePOIUi(WalkBottomUi.NONE)
    }

    fun updatePOIUi(type: WalkBottomUi) {
        _walkBottomUi.value = type
    }

    fun asyncWalkablePet() {
        viewModelScope.launch() {
            val response = walkRepository.walkablePetList(
                AUTH_KEY,
                AppConstants.ID
            )
            when (response) {
                is Resource.Success -> {
                    _walkablePetList.emit(response.value.data.pets)
                }
                is Resource.Failure -> {
                    Unit
                }
            }
        }
    }

    fun asyncMarkingDetail(id: Int) {
        viewModelScope.launch() {
            val response = walkRepository.markingDetail(
                AUTH_KEY,
                id
            )
            when (response) {
                is Resource.Success -> {
                    _markingDetail.emit(response.value.data)
                }
                is Resource.Failure -> {
                    Unit
                }
            }
        }
    }

    fun asyncPlaceDetail(id: Int) {
        viewModelScope.launch() {
            val response = walkRepository.placeDetail(
                AUTH_KEY,
                id
            )
            when (response) {
                is Resource.Success -> {
                    _placeDetail.emit(response.value.data)
                }
                is Resource.Failure -> {
                    Unit
                }
            }
        }
    }

    fun asyncPOIs() {
        LogUtil.log("TAG", "asyncPOIs")
        viewModelScope.launch() {
            var markingPoiList = emptyList<MarkingPoi>()
            var placePoiList = emptyList<PlacePoi.Places>()
            val deferreds = listOf(
                async {
                    val encryptLatitude = cameraPosition.value.latitude.encrypt()
                    val encryptLongitude = cameraPosition.value.longitude.encrypt()
                    val zoomLevel = 15 - cameraZoom.value.toInt()
                    val response = walkRepository.markingPoiList(
                        AUTH_KEY,
                        encryptLatitude,
                        encryptLongitude,
                        zoomLevel
                    )
                    when (response) {
                        is Resource.Success -> {
                            markingPoiList = response.value.data
                        }
                        is Resource.Failure -> {
                            Unit
                        }
                    }
                }, async {
                    val encryptLatitude = cameraPosition.value.latitude.encrypt()
                    val encryptLongitude = cameraPosition.value.longitude.encrypt()
                    val response = walkRepository.placePoiList(
                        AUTH_KEY,
                        encryptLatitude,
                        encryptLongitude
                    )
                    when (response) {
                        is Resource.Success -> {
                            placePoiList = response.value.data.places
                        }
                        is Resource.Failure -> {
                            Unit
                        }
                    }
                }
            )
            deferreds.awaitAll()
            _clearMarker.postValue(Event(true))
            _markingPOIs.postValue(Event(markingPoiList))
            _placePOIs.postValue(Event(placePoiList))
        }
    }

    fun asyncWalkId(petIds: List<Int>, lat: Double, lng: Double) {
        viewModelScope.launch() {
            val body = WalkStartRequest(AppConstants.ID, petIds, lat.encrypt(), lng.encrypt())
            val response = walkRepository.startWalk(
                AUTH_KEY,
                body
            )
            when (response) {
                is Resource.Success -> {
                    LogUtil.log("TAG", "response ${response.value}")
                    _isSucceedReadyForWalk.postValue(Event(response.value.data))
                }
                is Resource.Failure -> {
                    LogUtil.log("TAG", "response ${response.errorBody}")
                }

                is Resource.Error -> {
                    LogUtil.log("TAG", "response ${response.errorBody}")
                }
            }
        }
    }

    fun asyncWalkGuide(pageNo: Int) {
        LogUtil.log("TAG", "asyncWalkGuide $pageNo")
        viewModelScope.launch {
            val response = walkRepository.walkAudioGuide(
                AUTH_KEY,
                pageNo
            )

            when (response) {
                is Resource.Success -> {
                    addWalkGuide(response.value.data)
                }
                else -> {
                    //Do Nothing
                }
            }
        }
    }

    private suspend fun addWalkGuide(data: WalkAudioGuide) {
        LogUtil.log("TAG", "")
        data.audioGuideList?.let { items ->
            if (items.size > 0) {
                val localAudioList = getAudioGuideList(mContext)

                items.filter {
                    it.audioFileId.toString() in localAudioList
                }.map {
                    it.hasAudio = true
                }

                walkGuideListItem.addAll(items)
                hasMoreWalkGuideItem = data.listSize > walkGuideListItem.size
                walkGuidePageNo++

                _walkGuideListFlow.emit(walkGuideListItem)
            }
        }
//        if (data.audioGuideList?.size ?: 0 > 0) {
//            remoteAudioList?.forEach { remoteItem ->
//                localAudioList.forEach { localItem ->
//                    if (remoteItem.audioFileId.toString() == localItem.replace(".mp3", "")) {
//                        remoteItem.hasAudio = true
//                    }
//                }
//            }

    }

    fun downloadFile(context: Context, url: String, fileName: String, position: Int) {
        viewModelScope.launch {
            _readyToGuideProgress.postValue(Event(position))
            walkRepository.downLoadFileUrl(url).downloadToFileWithProgress(
                context.filesDir!!,
                fileName,
                position
            ).catch {
                LogUtil.log("TAG", "catch")
            }
                .collect { download ->
                    when (download) {
                        is Download.Progress -> {
                            _downloadProgress.emit(DownLoadProgress(download.percent, position))
                        }
                        is Download.Finished -> {
                            _downloadComplete.postValue(Event(download.file))
                        }
                    }
                }
        }
    }
}
