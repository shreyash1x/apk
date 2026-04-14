package com.ncorti.kotlin.template.app.recorder

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var currentFile: File? = null

    fun startRecording(phoneNumber: String?) {
        try {
            val fileName = generateFileName(phoneNumber)
            currentFile = File(context.filesDir, fileName)

            // Enable speakerphone for better capture (as suggested in PRD)
            audioManager.isSpeakerphoneOn = true

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(currentFile?.absolutePath)
                prepare()
                start()
            }
            Log.d("RecorderManager", "Recording started: ${currentFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("RecorderManager", "Error starting recording", e)
            stopRecording() // Reset state on error
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null
            audioManager.isSpeakerphoneOn = false
            Log.d("RecorderManager", "Recording stopped")
        } catch (e: Exception) {
            Log.e("RecorderManager", "Error stopping recording", e)
            mediaRecorder?.release()
            mediaRecorder = null
        }
    }

    private fun generateFileName(phoneNumber: String?): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val number = phoneNumber ?: "Unknown"
        return "Call_${number}_$timestamp.mp3"
    }
}
