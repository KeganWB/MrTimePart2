package com.example.mrtimepart2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive called")

        val builder = NotificationCompat.Builder(context!!, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.mr_time)
            .setContentTitle("Welcome")
            .setContentText("Welcome To MrTime, Hope you enjoy your stay")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.notify(1, builder.build())
    }
}
