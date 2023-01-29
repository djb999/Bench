package com.xdjbx.bench.domain.interfaces

import android.bluetooth.BluetoothDevice
import com.xdjbx.bench.domain.DeviceAction

interface BluetoothUpdateObserver: DeviceUpdateObserver {
    fun onReceive(deviceAction: DeviceAction, bluetoothDevice: BluetoothDevice)
}