package com.senex.androidlab1.views.activities.main.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.senex.androidlab1.R

internal fun Context.createNotificationChannel(
    idString: String,
    name: String,
    importance: Int,
) {
    val channel = NotificationChannel(idString, name, importance).apply {
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        vibrationPattern = longArrayOf(0, 1000) // wait before start, keep enabled
        setSound(
            Uri.parse("android.resource://$packageName/${R.raw.alert_misic_2}"),
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        )
    }

    getSystemServiceAs<NotificationManager>(AppCompatActivity.NOTIFICATION_SERVICE)
        .createNotificationChannel(channel)
}

internal inline fun <reified T> Context.createImplicitPendingIntent() = PendingIntent
    .getActivity(this,
        0,
        Intent(this, T::class.java),
        PendingIntent.FLAG_ONE_SHOT
    )

internal inline fun <reified T> Context.createImplicitBroadcastIntent(
    id: Int,
    notification: Notification,
): PendingIntent {
    val intent = Intent(this, T::class.java)
    intent.putExtra("notification_id", id)
    intent.putExtra("notification", notification)
    return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

internal fun Context.buildNotification(
    channelId: String,
    title: String,
    content: String,
    onClickIntent: PendingIntent?,
) = NotificationCompat
    .Builder(this, channelId)
    .setSmallIcon(R.drawable.ic_flask_primary_24)
    .setContentTitle(title)
    .setContentText(content)
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    .setAutoCancel(true)
    .setContentIntent(onClickIntent)
    .build()

internal fun Context.fireNotification(
    id: Int,
    notification: Notification,
): Unit = NotificationManagerCompat
    .from(this)
    .notify(id, notification)

internal fun Context.cancelNotification(
    id: Int,
): Unit = getSystemServiceAs<NotificationManager>(AppCompatActivity.NOTIFICATION_SERVICE)
    .cancel(id)

internal fun Context.wakeUpScreen() {
    val powerManager = getSystemServiceAs<PowerManager>(Context.POWER_SERVICE)

    if (!powerManager.isInteractive) {
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "senex.androidlab:notificationLock"
        )
        wakeLock.acquire(3000)
    }
}

internal inline fun <reified T> Context.getSystemServiceAs(name: String) =
    getSystemService(name) as T
