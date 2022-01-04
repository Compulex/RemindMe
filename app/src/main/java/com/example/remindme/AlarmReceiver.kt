package com.example.remindme

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var notificationManager : NotificationManager
    private var notificationId = 0
    private val primaryChannelId = "primary_notification_channel"

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationId = Date().time.toInt()
        notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        intent?.getStringExtra("msg")?.let { Log.i("MSG Passed In ", it) }
        val sendId = intent?.getStringExtra("id")

        val contextIntent = Intent(context, ViewReminder::class.java)
        contextIntent.putExtra("view", sendId)

        val contextPendingIntent = PendingIntent.getActivity(
            context, notificationId, contextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, primaryChannelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Remember: ")
            .setContentText(intent?.getStringExtra("msg"))
            .setContentIntent(contextPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contextPendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setDeleteIntent(contextPendingIntent) //when user swipes away notification still goes to page
        notificationManager.notify(notificationId, builder.build())
    }//onReceive

}