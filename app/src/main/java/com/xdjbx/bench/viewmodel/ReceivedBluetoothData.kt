package com.xdjbx.bench.viewmodel

import android.bluetooth.BluetoothDevice
import com.xdjbx.bench.domain.DeviceAction

data class ReceivedBluetoothData(
    val deviceAction: DeviceAction,
    val bluetoothDevice: BluetoothDevice
)


