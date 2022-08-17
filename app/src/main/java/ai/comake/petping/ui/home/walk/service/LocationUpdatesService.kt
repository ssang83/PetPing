/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.comake.petping.ui.home.walk.service

import ai.comake.petping.Event
import ai.comake.petping.MainActivity
import ai.comake.petping.R
import ai.comake.petping.data.db.walk.Walk
import ai.comake.petping.data.vo.AudioGuideItem
import ai.comake.petping.data.vo.AudioGuideStatus
import ai.comake.petping.data.vo.MyMarkingPoi
import ai.comake.petping.data.vo.WalkPath
import ai.comake.petping.google.database.room.walk.WalkDBRepository
import ai.comake.petping.ui.home.walk.EndState
import ai.comake.petping.util.*
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/*
 */
@AndroidEntryPoint
class LocationUpdatesService() : LifecycleService() {
    @Inject
    lateinit var walkRepository: WalkDBRepository
    private val mBinder: IBinder = LocalBinder()
    private var mChangingConfiguration = false
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mServiceHandler: Handler? = null
    private var mLocation: Location = Location(LocationManager.GPS_PROVIDER)
    private var mContext: Context? = null
    private var mWalkStatus = START
    private var mWalEndState = EndState.FORCE_END

    private var elapsedStartMilliSeconds = 0L
    private var elapsedPauseMilliSeconds = 0L

    private var walkPauseTimeMillis = 0L

    var wakeLock: PowerManager.WakeLock? = null

    val walkPhysicalPathList: StateFlow<ArrayList<WalkPath>>
        get() = _walkPathList

    val walkSpeedPathList: StateFlow<ArrayList<WalkPath>>
        get() = _walkPathList

    val elapsedStartPhysicalMilliSeconds: Long
        get() = elapsedStartMilliSeconds

    var walkPlayTimer: Job? = null
    var walkPauseTimer: Job? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        mContext = this
        createNotificationChannel()
        createFusedLocationClient()
        createLocationRequest()

        onDeviceCpu()
    }

    @SuppressLint("MissingSuperCall")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        val serviceStatus = intent?.getIntExtra(
            "status",
            1
        )

        LogUtil.log("TAG", "serviceStatus $serviceStatus")

        when (serviceStatus) {
            START -> {
                playWalkTimer()
                startForeground(NOTIFICATION_ID, notification())
            }
            PLAY -> {
                pauseWalk(false)
            }
            PAUSE -> {
                pauseWalk(true)
            }
        }

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        LogUtil.log("TAG", "")
        clearWalkResource()
        offDeviceCpu()
        super.onDestroy()
    }

    private fun clearWalkResource() {
        LogUtil.log("TAG", "")
        localWalkData = Walk()
        _walkTimeSeconds.value = "00:00"
        _isStartWalk.value = false
        _isPauseWalk.value = false
        _isStopWalkService.value = Event(false)
        _walkDistanceKm.value = "0.000"
        _walkPathList.value = ArrayList()
        _picturePaths.value = ArrayList()
        _myMarkingList.value = ArrayList()
        lifecycleScope.coroutineContext.cancelChildren()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBind(intent: Intent): IBinder {
        Log.i(TAG, "in onBind()")
        return mBinder
    }

    override fun onRebind(intent: Intent) {
        Log.i(TAG, "in onRebind()")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.i(TAG, "Last client unbound from service")
        return true // Ensures onRebind() is called when a client re-binds.
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun startLocationService() {
        LogUtil.log("TAG", "")
        val intent = Intent(this, LocationUpdatesService::class.java).apply {
            putExtra("status", mWalkStatus)
        }
        startService(intent)
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun stopLocationService() {
        LogUtil.log("TAG", "")
//        try {
        stopForeground(true)
        stopSelf()
//        } catch (unlikely: SecurityException) {
//            LogUtil.log(
//                "TAG",
//                "Lost location permission. Could not remove updates. $unlikely"
//            )
//        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun notification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("menu", "walk")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notificationLayout = RemoteViews(packageName, R.layout.view_notification)
        val serviceIntent = Intent(this, LocationUpdatesService::class.java)

        if (mWalkStatus == START || mWalkStatus == PLAY) {
            serviceIntent.putExtra("status", PAUSE)
            notificationLayout.setImageViewResource(R.id.status, R.drawable.ic_walk_pause)
            notificationLayout.setTextViewText(R.id.distance, "${_walkDistanceKm.value} Km")
        } else {
            serviceIntent.putExtra("status", PLAY)
            notificationLayout.setImageViewResource(R.id.status, R.drawable.ic_walk_play)
            notificationLayout.setTextViewText(R.id.distance, "일시중지")
        }
        val servicePendingIntent = PendingIntent.getService(
            this, 0, serviceIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationLayout.setOnClickPendingIntent(
            R.id.status,
            servicePendingIntent
        )
        val notificationBuilder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(this, CHANNEL_ID)
            } else {
                NotificationCompat.Builder(this)
            }
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_notification)
                .setWhen(System.currentTimeMillis())
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        return notificationBuilder.build()
    }

    fun startWalk(isStart: Boolean) {
        if (isStart) {
            mWalkStatus = START
            createLocationThread()
            startLocationService()
        } else {
            mWalkStatus = STOP
            stopLocationService()
            removeLocationThread()
            saveAndStopWalk()
        }
    }

    fun pauseWalk(isPause: Boolean) {
        if (isPause) {
            mWalkStatus = PAUSE
            startWalkPauseTimer()
            removeLocationThread()
        } else {
            mWalkStatus = PLAY
            startWalkPlayTimer()
            createLocationThread()
        }
        updateNotification()
        updateWalkStatusBroadcast()
    }

    //데이터베이스 저장및 종료
    private fun saveAndStopWalk() {
        lifecycleScope.launch(Dispatchers.IO) {
            saveLocalDatabase()
            _isStopWalkService.postValue(Event(true))
        }
        LogUtil.log("TAG", "")
    }

    suspend fun saveLocalDatabase() {
//        LogUtil.log("TAG", "mWalEndState ${mWalEndState.ordinal} ")
        if(localWalkData.walkId != -1){
            localWalkData.path = _walkPathList.value
            localWalkData.endState = mWalEndState.ordinal
            localWalkData.time = elapsedStartMilliSeconds.toHHMMSSFormat()
            localWalkData.walkEndDatetimeMilli = Date().time
            localWalkData.distance = _walkDistanceKm.value.toDouble()
            localWalkData.pictures = _picturePaths.value
            walkRepository.insert(localWalkData)
        }
    }

    private fun updateWalkStatusBroadcast() {
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_WALK_STATUS, mWalkStatus)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun updateWalkPathBroadcast() {
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, _walkPathList.value)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    private fun onNewLocation(location: Location) {
//        LogUtil.log("TAG", "")
        location?.let {
            mLocation = location
            val walkPath = WalkPath(
                mLocation,
                mWalkStatus,
                mLocation.latitude.encrypt(),
                mLocation.longitude.encrypt()
            )
            _walkPathList.value.add(walkPath)
            _walkDistanceKm.value = _walkPathList.value.calculateWalkDistance().format(3)
            updateNotification()
            updateWalkPathBroadcast()

            lifecycleScope.launch {
                saveLocalDatabase()
            }
        }

        // Notify anyone listening for broadcasts about the new location.
    }

    fun updateNotification() {
        mNotificationManager?.notify(
            NOTIFICATION_ID,
            notification()
        )
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }

    private fun createNotificationChannel() {
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager?.createNotificationChannel(mChannel)
        }
    }

    private fun createFusedLocationClient() {
        LogUtil.log("TAG", "")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mLocation = locationResult.lastLocation
                onNewLocation(mLocation)
            }

            override fun onLocationAvailability(isAvailability: LocationAvailability?) {
                super.onLocationAvailability(isAvailability)
                isAvailability?.isLocationAvailable?.let {
//                    mCallback?.onLocationAvailability(it)
                }
            }
        }
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        LogUtil.log("TAG", "")
        mLocationRequest = LocationRequest.create()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        //        mLocationRequest.setSmallestDisplacement(2.f);
    }

    private fun createLocationThread() {
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        try {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            LogUtil.log(
                "TAG",
                "Lost location permission. Could not request updates. $unlikely"
            )
        }
    }

    private fun removeLocationThread() {
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        mServiceHandler?.removeCallbacksAndMessages(null)
    }

    private fun startWalkPlayTimer() {
        walkPauseTimer?.cancel()
        playWalkTimer()
    }

    private fun startWalkPauseTimer() {
        elapsedPauseMilliSeconds = elapsedStartMilliSeconds
        walkPlayTimer?.cancel()
        pauseWalkTimer()
    }

    private fun playWalkTimer() {
        walkPlayTimer = lifecycleScope.launch(
            context = Dispatchers.IO
        ) {
            val timerStartTimeMillis = System.currentTimeMillis()
            while (true) {
                checkConditionsForStopWalking()
                elapsedStartMilliSeconds =
                    ((System.currentTimeMillis() - timerStartTimeMillis) + elapsedPauseMilliSeconds)
                _walkTimeSeconds.value = elapsedStartMilliSeconds.toWalkTimeFormat()
                delay(1000)
            }
        }
    }

    private fun pauseWalkTimer() {
        walkPauseTimer = lifecycleScope.launch(
            context = Dispatchers.IO
        ) {
            val timerStartTimeMillis = System.currentTimeMillis()
            while (true) {
                checkConditionsForStopWalking()
                walkPauseTimeMillis =
                    System.currentTimeMillis() - timerStartTimeMillis
                delay(1000)
            }
        }
    }

    private fun checkConditionsForStopWalking() {
        when (mWalkStatus) {
            START, PLAY -> {
                if (hasOverWalkSpeed(walkSpeedPathList.value)) {
                    mWalEndState = EndState.OVER_WALK_SPEED_END
                    startWalk(false)
                    return
                }

                if (hasOverTimePassedSinceTheStopWalk(
                        walkPhysicalPathList.value,
                        elapsedStartPhysicalMilliSeconds
                    )
                ) {
                    mWalEndState = EndState.NO_CHANGE_LOCATION_END
                    startWalk(false)
                    return
                }

                if (hasOverMaxWalkTime(elapsedStartMilliSeconds)) {
                    mWalEndState = EndState.MAX_WALK_TIME
                    startWalk(false)
                    return
                }
            }
            PAUSE -> {
                if (hasOverTimePassedSinceThePauseWalk(walkPauseTimeMillis)) {
                    mWalEndState = EndState.OVER_TIME_PAUSE_END
                    startWalk(false)
                    return
                }
            }
        }
    }

    //cpu 제한 방지 5시간10초 자동 해
    private fun onDeviceCpu() {
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PetPing::WalkService").apply {
                acquire(18010000)
            }
        }
    }

    private fun offDeviceCpu() {
        wakeLock?.release()
    }

    companion object {
        private val TAG = LocationUpdatesService::class.java.simpleName
        private const val PACKAGE_NAME =
            "LocationUpdatesService"

        /**
         * The name of the channel for notifications.
         */
        private const val CHANNEL_ID = "channel_01"
        const val ACTION_BROADCAST =
            "$PACKAGE_NAME.broadcast"
        const val EXTRA_LOCATION =
            "$PACKAGE_NAME.location"
        const val EXTRA_WALK_STATUS =
            "$PACKAGE_NAME.walk.status"
        const val START = 1
        const val PLAY = 2
        const val PAUSE = 3
        const val STOP = 4

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2

        /**
         * The identifier for the notification displayed for the foreground service.
         */
        private const val NOTIFICATION_ID = 111

        var localWalkData = Walk()
        var walkAudioGuideData = AudioGuideItem()

        var _picturePaths = MutableStateFlow(ArrayList<String>())

        val _isStartWalk = MutableStateFlow(false)
        val _isPauseWalk = MutableStateFlow(false)

        val _isStopWalkService = MutableLiveData(Event(false))

        val _cameraZoom = MutableStateFlow(8.0)
        val _walkDistanceKm = MutableStateFlow("0.00")
        val _walkPathList = MutableStateFlow(ArrayList<WalkPath>())
        val _myMarkingList = MutableStateFlow(ArrayList<MyMarkingPoi>())

        val _audioGuideStatus = MutableStateFlow(AudioGuideStatus())
        val _isWithAudioGuide = MutableStateFlow(true)
        var _lastLocation = Location("")

        val _cameraPosition = MutableStateFlow(LatLng(37.566573, 126.978179))

        val _walkTimeSeconds = MutableStateFlow("00:00")
    }
}