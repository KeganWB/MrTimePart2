package com.example.mrtimepart2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "onReceive called")
        val builder = NotificationCompat.Builder(context!!, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.mr_time)
            .setContentTitle("Notification Title")
            .setContentText("Notification Content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.notify(1, builder.build())
        Log.d("AlarmReceiver", "Notification sent from AlarmReceiver")
    }
}
