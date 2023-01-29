package com.xdjbx.bench.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.xdjbx.bench.R
import com.xdjbx.bench.core.VersionUtil
import com.xdjbx.bench.domain.interfaces.BluetoothUpdateObserver
import com.xdjbx.bench.domain.interfaces.LocationUpdateObserver
import com.xdjbx.bench.domain.interfaces.WifiUpdateObserver
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.activity.MainActivity

class DeviceService: Service() {

    private val SERVICE_CHANNEL_ID: String = "BENCH_SERVICE_CHANNEL"
    private val SERVICE_BACKGROUND_NOTIFICATION_ID: Int = 100

    private val binder = DeviceBinder()
    private lateinit var broadcastReceiver: GeneralBroadcastReceiver

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        // TODO - this will require the FOREGROUND_SERVICE service permission to be requested and means the starting of the
        // TODO - service will need to be deferred so for now leave it unsticky.
        // Make the service sticky
//        val notification = createNotification()
//        startForeground(SERVICE_BACKGROUND_NOTIFICATION_ID, notification)

        registerIntents()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, VersionUtil.retrievePendingIntentMutabilityFlag(false))

        val notification = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle("Device Service")
            .setContentText("Running in the background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        return notification
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