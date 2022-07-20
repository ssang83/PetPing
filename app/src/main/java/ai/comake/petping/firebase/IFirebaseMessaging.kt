package ai.comake.petping.firebase

import ai.comake.petping.MainActivity
import ai.comake.petping.R
import ai.comake.petping.util.LogUtil
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
import java.util.concurrent.atomic.AtomicInteger

class IFirebaseMessaging : FirebaseMessagingService() {
    private val INTENT_CODE = 1

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // airbridge app delete event
        if (message.data.containsKey("airbridge-uninstall-tracking")) {
            return
        }

        if (/* Check if data needs to be processed by long running job */ false) {
            // For long-running tasks (10 seconds or more) use WorkManager.
            scheduleJob(message)
        } else {
            // Handle message within 10 seconds
            handleNow(message)
        }
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
            registerToken()
        }
    }

    private fun sendNotification(message: RemoteMessage) {
        LogUtil.log("TAG", message.data.toString())
        val pendingIntent: PendingIntent?
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("type", message.data["type"] ?: "")
            putExtra("link", message.data["link"] ?: "")
            putExtra("title", message.data["title"] ?: "")
        }

        pendingIntent = PendingIntent.getActivity(
            this,
            INTENT_CODE,
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

    private fun registerToken() {
        //서버에 토큰 등록
    }

    companion object {
        private val CHANNEL_ID = "firebase_service"

        // 랜덤 유니크 ID
        private val c = AtomicInteger(0)
        fun getNotificationID() = c.incrementAndGet()
    }
}