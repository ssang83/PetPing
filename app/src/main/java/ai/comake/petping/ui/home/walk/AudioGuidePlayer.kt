package ai.comake.petping.ui.home.walk

import ai.comake.petping.data.vo.AudioGuideStatus
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.PAUSE
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion._walkStatus
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.toWalkTimeFormat
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AudioGuidePlayer(var mContext: Context?) {
    var telephonyManager: TelephonyManager? = null
    var mediaPlayer: MediaPlayer? = null
    var isCalling = false
    lateinit var mAudioManager: AudioManager
    lateinit var mFocusRequest: AudioFocusRequest

    fun setUpMediaIntentFilter() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        intentFilter.addAction(BLUETOOTH_HEADSET_ACTION)
        intentFilter.addAction(BLUETOOTH_HEADSET_STATE)
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)

        mContext?.registerReceiver(headsetReceiver, intentFilter)
    }

    fun setUpAudioFocus() {
        try {
            val mgr =
                mContext?.getSystemService(LifecycleService.TELEPHONY_SERVICE) as TelephonyManager
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            mAudioManager = mContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes).setAcceptsDelayedFocusGain(false).build()
            mAudioManager?.requestAudioFocus(mFocusRequest)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            try {
                isCalling = state == 2
                if (_walkStatus == PAUSE) {
                    return
                }

                if (_audioGuideStatus.value!!.getIsPlaying()) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                        if (!mediaPlayer?.isPlaying!!) {
                            mediaPlayer?.start()
                            _isPauseAudioGuide.value = false
                        }
                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        if (mediaPlayer?.isPlaying!!) {
                            mediaPlayer?.pause()
                            _isPauseAudioGuide.value = true
                        }
                    }
                    super.onCallStateChanged(state, incomingNumber)
                }
            } catch (e: Exception) {
                isCalling = false
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    fun setUpMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        mediaPlayer?.setOnErrorListener { _, _, _ ->
            false
        }

        mediaPlayer?.setOnCompletionListener {
            LogUtil.log("TAG", "setOnCompletionListener")
            if (it.currentPosition != 0) {
                _audioGuideStatus.value?.isEndAudio = true
                _endAudioGuide.value = true
            }
        }
    }

    fun startAudioGuide() {
        val uri =
            Uri.parse(mContext?.filesDir?.path + "/${_audioGuideStatus.value!!.audioFileId}.mp3")
        mediaPlayer?.setDataSource(
            uri.path
        )

        mediaPlayer?.isLooping = false
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            val duration = mediaPlayer?.duration ?: 0
            val totalPlaytime = duration.toWalkTimeFormat()
            _audioGuideStatus.value!!.setIsPlaying(true)
            _audioGuideStatus.value!!.setTotalTime(totalPlaytime)
            _startAudioGuide.value = true
            mediaPlayer?.start()
        }
    }

    fun pauseAudioGuide(isPause: Boolean) {
        if (isPause) {
            mediaPlayer?.pause()
            _isPauseAudioGuide.value = true
        } else {
            mediaPlayer?.start()
            _isPauseAudioGuide.value = false
        }
    }

    fun registerTelephonyCallback() {
        telephonyManager = mContext?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mContext?.mainExecutor?.let {
                telephonyManager?.registerTelephonyCallback(
                    it,
                    object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                        override fun onCallStateChanged(state: Int) {
                            when (state) {
                                TelephonyManager.CALL_STATE_IDLE -> {
                                    LogUtil.log("TAG", "CALL_STATE_IDLE")
                                    mediaPlayer?.start()
                                }
                                TelephonyManager.CALL_STATE_OFFHOOK -> {
                                    LogUtil.log("TAG", "CALL_STATE_OFFHOOK")
                                    mediaPlayer?.pause()
                                }
                                TelephonyManager.CALL_STATE_RINGING -> {
                                    //Nothing
                                }
                            }
                        }
                    })
            }

        } else {
            telephonyManager?.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE ->
                            mediaPlayer?.start()
                        TelephonyManager.CALL_STATE_OFFHOOK ->
                            mediaPlayer?.pause()
                        TelephonyManager.CALL_STATE_RINGING -> {
                            //Nothing
                        }
                    }

                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private var headsetConnected = false
    private val headsetReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val headSetState: Int?

            if (!headsetConnected) {
                headsetConnected = true
                return
            }

            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                headSetState = intent?.extras?.getInt("state")
                if (headSetState == 0) {
                    headsetConnected = false
                    mediaPlayer?.pause()
                    _isPauseAudioGuide.value = true
                } else if (headSetState == 1) {
                    headsetConnected = true
                    mediaPlayer?.start()
                    _isPauseAudioGuide.value = false
                }
            } else if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                headSetState = intent?.extras?.getInt("android.bluetooth.profile.extra.STATE")
                if (headSetState == 0) {
                    mediaPlayer?.pause()
                    _isPauseAudioGuide.value = true
                } else if (headSetState == 2) {
                    mediaPlayer?.start()
                    _isPauseAudioGuide.value = false
                }
            }
        }
    }

    fun clearAudioResource() {
        isCalling = false
        mediaPlayer?.stop()
        mediaPlayer?.release()

        _audioGuideStatus.value = AudioGuideStatus()
    }

    companion object {
        private const val BLUETOOTH_HEADSET_ACTION =
            "android.bluetooth.headset.action.STATE_CHANGED"
        private const val BLUETOOTH_HEADSET_STATE = "android.bluetooth.headset.extra.STATE"
        private const val MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON"
        val _audioGuideStatus = MutableLiveData(AudioGuideStatus())
        val _startAudioGuide = MutableLiveData(false)
        val _endAudioGuide = MutableLiveData(false)
        val _isPauseAudioGuide = MutableLiveData(false)
    }
}