package com.xdjbx.bench.domain.interfaces

interface DeviceUpdateObserver {
    fun onReceiveError(exception: Exception)
}