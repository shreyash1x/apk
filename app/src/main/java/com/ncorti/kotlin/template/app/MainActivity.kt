package com.ncorti.kotlin.template.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ncorti.kotlin.template.app.databinding.ActivityMainBinding
import com.ncorti.kotlin.template.app.data.Recording
import com.ncorti.kotlin.template.app.service.RecordingService
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val recordingList = mutableListOf<Recording>()
    private lateinit var adapter: RecordingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadRecordings()

        binding.buttonStartService.setOnClickListener {
            if (checkPermissions()) {
                startRecordingService()
            } else {
                requestPermissions()
            }
        }

        binding.buttonStopService.setOnClickListener {
            stopRecordingService()
        }
    }

    private fun setupRecyclerView() {
        adapter = RecordingAdapter(recordingList)
        binding.recyclerViewRecordings.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecordings.adapter = adapter
    }

    private fun loadRecordings() {
        val files = filesDir.listFiles { _, name -> name.startsWith("Call_") && name.endsWith(".mp3") }
        recordingList.clear()
        files?.forEach { file ->
            val nameWithoutExt = file.name.removePrefix("Call_").removeSuffix(".mp3")
            val parts = nameWithoutExt.split("_")
            if (parts.size >= 3) {
                val phoneNumber = parts[0]
                val timestamp = "${parts[1]} ${parts[2]}"
                recordingList.add(Recording(file, phoneNumber, timestamp))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun startRecordingService() {
        val intent = Intent(this, RecordingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopRecordingService() {
        val intent = Intent(this, RecordingService::class.java)
        stopService(intent)
    }

    private fun checkPermissions(): Boolean {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL)
        }

        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL)
        }
        
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
    }

    override fun onResume() {
        super.onResume()
        loadRecordings()
    }

    inner class RecordingAdapter(private val recordings: List<Recording>) :
        RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val phoneNumber: TextView = view.findViewById(android.R.id.text1)
            val timestamp: TextView = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.id.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val recording = recordings[position]
            holder.phoneNumber.text = "Number: ${recording.phoneNumber}"
            holder.timestamp.text = "Time: ${recording.timestamp}"
        }

        override fun getItemCount() = recordings.size
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
