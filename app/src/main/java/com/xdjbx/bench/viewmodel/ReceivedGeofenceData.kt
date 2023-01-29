package com.xdjbx.bench.viewmodel

import com.google.android.gms.location.Geofence
import com.xdjbx.bench.domain.DeviceAction

data class ReceivedGeofenceData(
    val deviceAction: DeviceAction,
    val transitionGeofences: List<Geofence>
)