package com.example.tokenproject.FCMService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.tokenproject.MainActivity
import com.example.tokenproject.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("TAG", "onNewToken : $s")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.size > 0) {
            val title : String = remoteMessage.data["title"].toString()
            val message = remoteMessage.data["message"]
            //String test = remoteMessage.getData().get("test");
            sendNotification(title, message)
        }
    }
    private fun sendNotification(title: String, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = "Mook"
            val channel_name = "MookChat"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelMessage =
                NotificationChannel(channel, channel_name, NotificationManager.IMPORTANCE_DEFAULT)
            channelMessage.description = "MookChat Notification"
            channelMessage.enableLights(true)
            channelMessage.enableVibration(true)
            channelMessage.setShowBadge(false)
            channelMessage.vibrationPattern = longArrayOf(1000, 1000)
            notificationManager.createNotificationChannel(channelMessage)

            //푸시알림을 Builder를 이용하여 만듭니다.
            val notificationBuilder =
                NotificationCompat.Builder(this, channel)
                    .setSmallIcon(R.drawable.ic_baseline_chat_24) // 푸시알림 아이콘
                    .setContentTitle(title) // 푸시알림의 제목
                    .setContentText(message) // 푸시알림의 내용
                    .setChannelId(channel)
                    .setAutoCancel(true) //선택시 자동으로 삭제되도록 설정
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent) //알림을 눌렀을때 실행할 인텐트 설정
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            val notificationManager1 =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager1.notify(9999, notificationBuilder.build())
        } else {
            val notificationBuilder =
                NotificationCompat.Builder(this, "")
                    .setSmallIcon(R.drawable.ic_baseline_chat_24)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(9999, notificationBuilder.build())
        }
    }



}