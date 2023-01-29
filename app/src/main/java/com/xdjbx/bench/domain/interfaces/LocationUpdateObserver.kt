package com.xdjbx.bench.domain.interfaces

import com.google.android.gms.location.Geofence
import com.xdjbx.bench.domain.DeviceAction

interface LocationUpdateObserver: DeviceUpdateObserver {
    fun onReceive(deviceAction: DeviceAction, transitionGeofences: List<Geofence>)
}