package ai.comake.petping.firebase

import ai.comake.petping.*
import ai.comake.petping.AppConstants.FCM_TOKEN
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import co.ab180.airbridge.Airbridge
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class IFirebaseMessaging : FirebaseMessagingService() {
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var INTENT_CODE = 1

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // airbridge app delete event
        if (message.data.containsKey("airbridge-uninstall-tracking")) {
            return
        }

        handleNow(message)

//        if (/* Check if data needs to be processed by long running job */ false) {
//            // For long-running tasks (10 seconds or more) use WorkManager.
//            scheduleJob(message)
//        } else {
//            // Handle message within 10 seconds
//            handleNow(message)
//        }
    }

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob(message: RemoteMessage) {
        val work = OneTimeWorkRequest.Builder(IJobWorker::class.java).build()
        WorkManager.getInstance(applicationContext).beginWith(work).enqueue()
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow(message: RemoteMessage) {
        LogUtil.log("TAG", "Short lived task is done.")
        sendNotification(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        LogUtil.log("TAG", "token $token")
        Airbridge.registerPushToken(token)
        if (token.isNotEmpty()) {
            saveTokenDataStore(token)
        }
    }

    private fun sendNotification(message: RemoteMessage) {
        LogUtil.log("TAG", message.data.toString())
        val pendingIntent: PendingIntent?
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("type", message.data["type"] ?: "")
            putExtra("link", message.data["link"] ?: "")
            putExtra("title", message.data["title"] ?: "")
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        LogUtil.log("TAG2", "message.data[\"type\"] : ${message.data["type"] }")
        LogUtil.log("TAG2", "message.data[\"link\"] : ${message.data["link"] }")

        pendingIntent = PendingIntent.getActivity(
            this,
            INTENT_CODE++,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setContentTitle(message.notification?.title ?: "")
            .setContentText(message.notification?.body ?: "")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.notification?.body ?: ""))
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "펫핑 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        if (message.data["img"].isNullOrEmpty()) {
            notificationManager.notify(getNotificationID(), notificationBuilder.build())
        } else {
            val imageUrl = message.data["img"]
            Glide.with(applicationContext)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        notificationManager.notify(
                            getNotificationID(),
                            notificationBuilder.build()
                        )
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        notificationBuilder.setLargeIcon(resource)
                        notificationBuilder.setStyle(
                            NotificationCompat.BigPictureStyle().bigPicture(resource)
                                .bigLargeIcon(null)
                        )

                        notificationManager.notify(
                            getNotificationID(),
                            notificationBuilder.build()
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //do nothing
                    }
                })
        }
    }

    private fun saveTokenDataStore(token: String) {
        sharedPreferencesManager.saveFCMTokenDataStore(token)
    }

    companion object {
        private val CHANNEL_ID = "firebase_service"

        // 랜덤 유니크 ID
        private val c = AtomicInteger(0)
        fun getNotificationID() = c.incrementAndGet()
    }
}