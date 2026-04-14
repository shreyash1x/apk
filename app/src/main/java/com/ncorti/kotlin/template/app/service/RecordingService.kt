package com.ncorti.kotlin.template.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ncorti.kotlin.template.app.MainActivity
import com.ncorti.kotlin.template.app.R
import com.ncorti.kotlin.template.app.recorder.RecorderManager
import com.ncorti.kotlin.template.app.telephony.CallStateManager

class RecordingService : Service() {
    private lateinit var callStateManager: CallStateManager
    private lateinit var recorderManager: RecorderManager

    override fun onCreate() {
        super.onCreate()
        recorderManager = RecorderManager(this)
        callStateManager = CallStateManager(
            this,
            onCallStarted = { phoneNumber ->
                if (checkPermissions()) {
                    recorderManager.startRecording(phoneNumber)
                }
            },
            onCallEnded = {
                recorderManager.stopRecording()
            }
        )
        callStateManager.startListening()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun checkPermissions(): Boolean {
        val permissions = mutableListOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_PHONE_STATE
        )
        return permissions.all {
            androidx.core.content.ContextCompat.checkSelfPermission(this, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        callStateManager.stopListening()
        recorderManager.stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.recording_service_title))
            .setContentText(getString(R.string.recording_service_content))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Call Recording Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "RecordingServiceChannel"
        private const val NOTIFICATION_ID = 1
    }
}
