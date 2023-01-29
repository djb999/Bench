package com.xdjbx.bench.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.BluetoothUpdateObserver
import com.xdjbx.bench.service.DeviceService

class BluetoothViewModel : DeviceBaseViewModel(), BluetoothUpdateObserver {

    private val _receivedBluetoothData = MutableLiveData<ReceivedBluetoothData>()
    val receivedBluetoothData: LiveData<ReceivedBluetoothData> = _receivedBluetoothData

    override fun onReceive(deviceAction: DeviceAction, bluetoothDevice: BluetoothDevice) {
        _receivedBluetoothData.postValue(ReceivedBluetoothData(deviceAction, bluetoothDevice))
    }

    fun addBluetoothObserver(deviceService: DeviceService) {
        _deviceService.postValue(deviceService)
        _deviceService.value?.addBluetoothObserver(this)
    }

    private fun removeBluetoothObserver() {
        _deviceService.value?.let {
            it.removeBluetoothObserver(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeBluetoothObserver()
    }
}