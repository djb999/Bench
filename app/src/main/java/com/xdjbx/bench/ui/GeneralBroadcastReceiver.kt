package com.xdjbx.bench.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.BluetoothUpdateObserver
import com.xdjbx.bench.domain.interfaces.LocationUpdateObserver
import com.xdjbx.bench.domain.interfaces.WifiUpdateObserver
import com.xdjbx.bench.processor.GeofenceProcessor.isGeofenceEntered
import com.xdjbx.bench.processor.GeofenceProcessor.isGeofenceTransitioned
import com.xdjbx.bench.ui.fragment.WifiFragment

object GeneralBroadcastReceiver : BroadcastReceiver() {

    // TODO - implement validation for MissingPermission to avoid mishaps on getConnectionState below
    val TAG = "GeneralBroadcastReceiver"

    private val bluetoothObservers = mutableListOf<BluetoothUpdateObserver>()
    private val wifiObservers = mutableListOf<WifiUpdateObserver>()
    private val locationObservers = mutableListOf<LocationUpdateObserver>()

    var wifiManager: WifiManager? = null

    @SuppressLint("LongLogTag", "MissingPermission")
    override fun onReceive(context: Context?, onReceiveIntent: Intent?) {

        wifiManager =
            context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        var bluetoothAction = false
        var wifiAction = false

        onReceiveIntent?.let { intent ->
            val action = intent.action

            if (isGeofencingIntent(intent)) {
                val geofencingEvent = GeofencingEvent.fromIntent(intent)
                if (geofencingEvent.hasError()) {
                    val errorMessage = GeofenceStatusCodes
                        .getStatusCodeString(geofencingEvent.errorCode)
                    Log.e(TAG, errorMessage)
                    return
                }

                // Get the transition type.
                val geofenceTransition = geofencingEvent.geofenceTransition

                // Test that the reported transition was of interest.
                if (isGeofenceTransitioned(geofenceTransition)
                ) {

                    // Get the geofences that were triggered. A single event can trigger
                    // multiple geofences.
                    val triggeringGeofences = geofencingEvent.triggeringGeofences
                    locationObservers.forEach {
                        it.onReceive(
                            if (isGeofenceEntered(geofenceTransition)) DeviceAction.ENTERED else DeviceAction.EXITED ,
                            triggeringGeofences
                        )
                    }

                } else {
                    // Log the error.
                    Log.e(
                        TAG, "Invalid transition"
                        )

                }

            } else {
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        //Device found
                        var bluetoothAction = true
                        Log.d(TAG, "XBTHX - onReceive: ACTION_FOUND")
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        //Device is now connected
                        var bluetoothAction = true

                        Log.d(TAG, "XBTHX - onReceive: ACTION_ACL_CONNECTED")

                        retrieveBluetoothConnection(intent)?.let {
                            Log.d(TAG, "XBTHX - onReceive: updating bluetooth observers with Connected status")
                            updateBluetoothObservers(it, DeviceAction.CONNECTED)
                        }

                        //                    context?.let { it1 -> LocalNotificationManager(it1).notifyMe("Take your towel") }
                    }
//                BluetoothDevice.ACTION_DISCOVERY_FINISHED -> {
//                    //Done searching
//                    Log.d(TAG, "XBTHX - onReceive: ACTION_DISCOVERY_FINISHED")
//                }
                    BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED -> {
                        //Device is about to disconnect
                        var bluetoothAction = true

                        Log.d(TAG, "XBTHX - onReceive: ACTION_ACL_DISCONNECT_REQUESTED")
                    }
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        //Device has disconnected
                        var bluetoothAction = true

                        retrieveBluetoothConnection(intent)?.let {
                            Log.d(TAG, "XBTHX - onReceive: updating bluetooth observers with Disconnected status")
                            updateBluetoothObservers(it, DeviceAction.DISCONNECTED)
                        }

                        Log.d(TAG, "XBTHX - onReceive: ACTION_ACL_DISCONNECTED")
                    }
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        var wifiAction = true
                        Log.d(TAG, "XBTHX - onReceive: SCAN_RESULTS_AVAILABLE_ACTION")
                        val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)

                        } else {
                            TODO("VERSION.SDK_INT < M")
                        }
                        if (success) {
                            val results = wifiManager?.scanResults
                            Log.d(
                                WifiFragment.TAG,
                                "XWFIX - onReceive: SCAN_RESULTS_AVAILABLE_ACTION - success - results size: ${results?.size}"
                            )

                            Log.d(
                                WifiFragment.TAG,
                                "XWFIX - onReceive: SCAN_RESULTS_AVAILABLE_ACTION - success - update observers"
                            )
                            updateWifiObservers(DeviceAction.GET_DEVICE_LIST)
                        } else {
                            Log.d(
                                WifiFragment.TAG,
                                "XWFIX - onReceive: SCAN_RESULTS_AVAILABLE_ACTION - failed"
                            )
                            scanFailure()
                        }
                    }
                    else -> {
                        //Device - other action
                        var bluetoothAction = true
                        Log.d(TAG, "XBTHX - onReceive: Other action not accounted for")
                    }
                }

            }

        }

    }


    private fun isGeofencingIntent(intent: Intent): Boolean {
        return (intent.action.equals("android.intent.action.GEOFENCE_TRANSITION"))
    }

    private fun updateBluetoothObservers(
        bluetoothDevice: BluetoothDevice,
        deviceStatus: DeviceAction
    ) {
        bluetoothObservers.forEach {
            it.onReceive(
                deviceStatus,
                bluetoothDevice
            )
        }
    }

    private fun updateWifiObservers(
        deviceStatus: DeviceAction
    ) {
        val wifiResults = scanSuccess()

        wifiObservers.forEach {
            it.onReceive(
                deviceStatus,
                wifiResults
            )
        }
    }

    fun addBluetoothObserver(observer: BluetoothUpdateObserver) {
        bluetoothObservers.add(observer)
    }

    fun removeBluetoothObserver(observer: BluetoothUpdateObserver) {
        bluetoothObservers.remove(observer)
    }

    fun addWifiObserver(observer: WifiUpdateObserver) {
        wifiObservers.add(observer)
    }

    fun removeWifiObserver(observer: WifiUpdateObserver) {
        wifiObservers.remove(observer)
    }

    fun addLocationObserver(observer: LocationUpdateObserver) {
        locationObservers.add(observer)
    }

    fun removeLocationObserver(observer: LocationUpdateObserver) {
        locationObservers.remove(observer)
    }

    private fun scanSuccess(): List<ScanResult> {
        return wifiManager?.scanResults ?: mutableListOf()
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
//        val results = wifiManager.scanResults
        // potentially use older scan results ...
    }

    private fun retrieveBluetoothConnection(
        it: Intent
    ): BluetoothDevice? {
        return it.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }


}