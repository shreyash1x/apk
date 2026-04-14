package com.ncorti.kotlin.template.app.telephony

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

class CallStateManager(
    private val context: Context,
    private val onCallStarted: (String?) -> Unit,
    private val onCallEnded: () -> Unit
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            handleCallState(state, phoneNumber)
        }
    }

    private val telephonyCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        object : TelephonyCallback(), TelephonyCallback.CallStateListener {
            override fun onCallStateChanged(state: Int) {
                // Note: phoneNumber is not available in CallStateListener for Android 12+
                handleCallState(state, null)
            }
        }
    } else null

    private fun handleCallState(state: Int, phoneNumber: String?) {
        when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                Log.d("CallStateManager", "Call Offhook: $phoneNumber")
                onCallStarted(phoneNumber)
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                Log.d("CallStateManager", "Call Idle")
                onCallEnded()
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                Log.d("CallStateManager", "Call Ringing: $phoneNumber")
            }
        }
    }

    fun startListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback?.let {
                telephonyManager.registerTelephonyCallback(context.mainExecutor, it)
            }
        } else {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    fun stopListening() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyCallback?.let {
                telephonyManager.unregisterTelephonyCallback(it)
            }
        } else {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
    }
}
