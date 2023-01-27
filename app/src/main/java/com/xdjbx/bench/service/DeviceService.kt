package com.xdjbx.bench.service

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import com.xdjbx.bench.domain.interfaces.BluetoothUpdateObserver
import com.xdjbx.bench.domain.interfaces.LocationUpdateObserver
import com.xdjbx.bench.domain.interfaces.WifiUpdateObserver
import com.xdjbx.bench.ui.GeneralBroadcastReceiver

class DeviceService: Service() {

    private val binder = DeviceBinder()
    private lateinit var broadcastReceiver: GeneralBroadcastReceiver

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        registerIntents()
    }

    fun retrieveBroadcastReceiver(): GeneralBroadcastReceiver {
        return broadcastReceiver
    }

    fun addBluetoothObserver(observer: BluetoothUpdateObserver) {
        broadcastReceiver.addBluetoothObserver(observer)
    }

    fun removeBluetoothObserver(observer: BluetoothUpdateObserver) {
        broadcastReceiver.removeBluetoothObserver(observer)
    }

    fun addWifiObserver(observer: WifiUpdateObserver) {
        broadcastReceiver.addWifiObserver(observer)
    }

    fun removeWifiObserver(observer: WifiUpdateObserver) {
        broadcastReceiver.removeWifiObserver(observer)
    }

    fun addLocationObserver(observer: LocationUpdateObserver) {
        broadcastReceiver.addLocationObserver(observer)
    }

    fun removeLocationObserver(observer: LocationUpdateObserver) {
        broadcastReceiver.removeLocationObserver(observer)
    }


    // TODO - move intent registration and monitoring to a service
    fun registerIntents() {
        // Initialize the broadcast receiver
        broadcastReceiver = GeneralBroadcastReceiver

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        // Register the broadcast receiver
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
    inner class DeviceBinder : Binder() {
        fun getService(): DeviceService = this@DeviceService
    }

}