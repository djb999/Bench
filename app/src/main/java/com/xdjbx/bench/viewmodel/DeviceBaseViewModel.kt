package com.xdjbx.bench.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xdjbx.bench.domain.interfaces.DeviceUpdateObserver
import com.xdjbx.bench.service.DeviceService

open class DeviceBaseViewModel: ViewModel(), DeviceUpdateObserver {

    protected val _deviceService = MutableLiveData<DeviceService>()

    protected val _serviceReceiveException = MutableLiveData<Exception>()
    val serviceReceiveException: LiveData<Exception> = _serviceReceiveException

    override fun onReceiveError(exception: Exception) {
        _serviceReceiveException.postValue(exception)
    }

}