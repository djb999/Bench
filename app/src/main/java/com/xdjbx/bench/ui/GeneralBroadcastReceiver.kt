package com.xdjbx.bench.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.xdjbx.bench.notification.LocalNotificationManager


class GeneralBroadcastReceiver: BroadcastReceiver() {

    // TODO - implement validation for MissingPermission to avoid mishaps on getConnectionState below

    @SuppressLint("LongLogTag", "MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.let {
            val action = it.action
            val bluetoothDevice = it.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            bluetoothDevice?.let { bluetoothDevice: BluetoothDevice ->

                val bluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                val connected = bluetoothManager.getConnectionState(bluetoothDevice, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED
                Log.d(TAG, "XBTHX - onReceive: device is connected: $connected")

            }

            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //Device found
                    Log.d(TAG, "XBTHX - onReceive: ACTION_FOUND")
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    //Device is now connected
                    Log.d(TAG, "XBTHX - onReceive: ACTION_ACL_CONNECTED")
                    context?.let { it1 -> LocalNotificationManager(it1).notifyMe() }
                }
//                BluetoothDevice.ACTION_DISCOVERY_FINISHED -> {
//                    //Done searching
//                    Log.d(TAG, "XBTHX - onReceive: ACTION_DISCOVERY_FINISHED")
//                }
                BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED -> {
                    //Device is about to disconnect
                    Log.d(TAG, "XBTHX - onReceive: ACTION_ACL_DISCONNECT_REQUESTED")
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    //Device has disconnected
                    Log.d(TAG, "XBTHX - onReceive: ACTION_ACL_DISCONNECTED")
                }
                else -> {
                    //Device - other action
                    Log.d(TAG, "XBTHX - onReceive: Other action not accounted for")
                }
            }
        }

    }

    companion object {
        val TAG = "GeneralBroadcastReceiver"
    }
}