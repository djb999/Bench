package com.xdjbx.bench.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xdjbx.bench.service.DeviceService

class DeviceSharedViewModel : ViewModel() {

    private val _eventList = MutableLiveData<MutableList<String>>()
    val eventList: LiveData<MutableList<String>> = _eventList

    private val _deviceService = MutableLiveData<DeviceService>()

    init {
        _eventList.value = mutableListOf()
    }

    val deviceService: LiveData<DeviceService>
        get() = _deviceService

    fun addService(deviceService: DeviceService) {
        _deviceService.postValue(deviceService)
    }

    fun addEvent(event: String) {
        val list = _eventList.value ?: mutableListOf()
        list.add(event)
        _eventList.value = list
    }

    fun clearEvents() {
        _eventList.value = mutableListOf()
    }
}