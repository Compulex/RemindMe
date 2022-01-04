package com.example.remindme

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity


class AlarmNotification(var context: Context, var notify: String, var id: String, var amtNum: Int,
                        var timeLength: String) {
    private val notificationId = 0
    private val primaryChannelId = "primary_notification_channel"
    private lateinit var notifyIntent : Intent
    private lateinit var notifyPendingIntent : PendingIntent
    private lateinit var alarmManager: AlarmManager

    fun createNotificationChannel(){
        val notificationManager : NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                primaryChannelId, "Alarm notification",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.description = "reminder alarm set"
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }//createNotificationChannel

    fun scheduleNotification(){
        notifyIntent = Intent(context, AlarmReceiver::class.java)

        //add text to notification
        notifyIntent.putExtra("msg", notify)
        notifyIntent.putExtra("id", id)

        notifyPendingIntent = PendingIntent.getBroadcast(context, notificationId, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        setAlarm()
    }//scheduleNotification

    private fun setAlarm(){
        val specifiedTime = when(timeLength){
            "seconds" -> amtNum * 1000
            "minutes" -> amtNum * 60000
            "hours" -> amtNum * 60 * 60000
            "days" -> amtNum * 24 * 60 * 60000
            "weeks" -> amtNum * 7 * 24 * 60 * 60000
            "months" -> amtNum * 4.345.toInt() * 7 * 24 * 60 * 60000 //estimate
            else -> { 0 }
        }//set time for alarm

        //regular
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + specifiedTime, notifyPendingIntent)

        //may have feature for update
        //repeat
        /*if(onRepeat){
            val repInterval = specifiedTime.toLong()
            val triggerTime = SystemClock.elapsedRealtime() + repInterval

            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repInterval,
                notifyPendingIntent)
        }
        else{
            //regular
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + specifiedTime,
                notifyPendingIntent)
        }*/
    }//setAlarm
}