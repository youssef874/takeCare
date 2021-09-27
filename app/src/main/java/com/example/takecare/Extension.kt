package com.example.takecare

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.takecare.ui.MyAppointmentFragment
import com.google.android.material.textfield.TextInputLayout

/**
 * This an extension function to handle an bad user input
 * @param isWrong: the input status
 * @param msg: the message to display if the input bad
 */
fun TextInputLayout.setError(isWrong: Boolean, msg: String) {
    if (isWrong) {
        isErrorEnabled = true
        error = msg
    }else{
        isErrorEnabled = false
        error = ""
    }
}


private val NOTIFICATION_ID = 0

/**
 * This function to create and send the notification
 */
fun NotificationManager.sendNotification(message: String, applicationContext: Context){

    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    //This intent allow other app to open this app activity
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    //create the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.appointment_notification_id)
    ).setSmallIcon(R.drawable.ic_baseline_healing_24)
        .setContentTitle(applicationContext.getString(R.string.request_appointment))
        .setContentText(message)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
    //send the notification
    notify(NOTIFICATION_ID,builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
