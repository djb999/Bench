package com.xdjbx.bench.domain.interfaces

import android.bluetooth.BluetoothDevice
import android.net.wifi.ScanResult
import com.google.android.gms.location.Geofence
import com.xdjbx.bench.domain.DeviceAction

interface LocationUpdateObserver {
    fun onReceive(deviceAction: DeviceAction, transitionGeofences: List<Geofence>)
}