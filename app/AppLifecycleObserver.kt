package com.example.mrtimepart2

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class AppLifecycleObserver(private val context: Context) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.d("AppLifecycleObserver", "App moved to background")
        sendNotification()
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.mr_time)
            .setContentTitle("Did you forget something?")
            .setContentText("Your app is still running in the background")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("AppLifecycleObserver", "Permission for POST_NOTIFICATIONS not granted")
            return
        }
        notificationManager.notify(2, builder.build())
        Log.d("AppLifecycleObserver", "Background notification sent")
    }
}
