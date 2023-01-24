package com.xdjbx.bench.domain.interfaces

import android.bluetooth.BluetoothDevice
import android.net.wifi.ScanResult
import com.xdjbx.bench.domain.DeviceAction

interface WifiUpdateObserver {
    fun onReceive(deviceAction: DeviceAction, scanResults: List<ScanResult>)
}