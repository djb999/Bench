package com.xdjbx.bench.domain

sealed class DeviceAction() {
    object CONNECTED: DeviceAction()
    object DISCONNECTED: DeviceAction()
    object GET_DEVICE_LIST: DeviceAction()
    object ENTERED: DeviceAction()
    object EXITED: DeviceAction()

}