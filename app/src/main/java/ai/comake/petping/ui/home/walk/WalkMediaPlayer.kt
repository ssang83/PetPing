package ai.comake.petping.ui.home.walk

import ai.comake.petping.ui.home.walk.service.LocationUpdatesService.Companion.walkAudioGuideData
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

class WalkMediaPlayer(val mContext: Context) {

    var mAudioManager: AudioManager? = null
    var mFocusRequest: AudioFocusRequest? = null
    var telephonyManager: TelephonyManager? = null
    var mediaPlayer: MediaPlayer? = null

    private fun setUpMediaIntentFilter() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        intentFilter.addAction(BLUETOOTH_HEADSET_ACTION)
        intentFilter.addAction(BLUETOOTH_HEADSET_STATE)
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)

        mContext.registerReceiver(headsetReceiver, intentFilter)
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
//                walkStateListener?.onWalkGuideEnd()
            }
        }
    }

    fun startGuideMusic() {
        val uri = Uri.parse(mContext.filesDir.path + "/${walkAudioGuideData.id}.mp3")
        mediaPlayer?.setDataSource(
            uri.path
        )

        mediaPlayer?.isLooping = false
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            val duration = mediaPlayer?.duration ?: 0
            val totalPlaytime = duration.toWalkTimeFormat()
//            walkStateListener?.onWalkGuideDuration(totalPlaytime)
            mediaPlayer?.start()
        }
    }

    fun playGuideMusic() {
        LogUtil.log("TAG", "")
        mediaPlayer?.start()
//        walkStateListener?.isWalkGuidePause(false)
    }


    fun pauseGuideMusic() {
        mediaPlayer?.pause()
//        walkStateListener?.isWalkGuidePause(true)
    }

    fun registerTelephonyCallback() {
        telephonyManager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager?.registerTelephonyCallback(
                mContext.mainExecutor,
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
//                        walkStateListener?.isWalkGuidePause(true)
                } else if (headSetState == 1) {
                    headsetConnected = true
                    mediaPlayer?.start()
//                        walkStateListener?.isWalkGuidePause(false)
                }
            } else if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                headSetState = intent?.extras?.getInt("android.bluetooth.profile.extra.STATE")
                if (headSetState == 0) {
                    mediaPlayer?.pause()
//                        walkStateListener?.isWalkGuidePause(true)
                } else if (headSetState == 2) {
                    mediaPlayer?.start()
//                        walkStateListener?.isWalkGuidePause(false)
                }
            }
        }
    }

    companion object {
        private const val BLUETOOTH_HEADSET_ACTION =
            "android.bluetooth.headset.action.STATE_CHANGED"
        private const val BLUETOOTH_HEADSET_STATE = "android.bluetooth.headset.extra.STATE"
        private const val MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON"
    }
}