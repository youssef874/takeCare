package com.example.takecare.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.takecare.R
import com.example.takecare.sendNotification

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val notificationManager = ContextCompat.getSystemService(
            p0!!,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
                p0.getText(R.string.notification_channel_message).toString(),
                p0
            )

    }
}