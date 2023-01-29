package com.xdjbx.bench.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.Geofence
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.LocationUpdateObserver
import com.xdjbx.bench.service.DeviceService
import com.xdjbx.bench.ui.data.GeofenceEntry
import com.xdjbx.bench.ui.fragment.LocationFragment

class LocationViewModel : DeviceBaseViewModel(), LocationUpdateObserver {

    private val _geofenceList = MutableLiveData<MutableList<Geofence>>()
    val geofenceList: LiveData<MutableList<Geofence>> = _geofenceList

    private val _geofenceEntryList = MutableLiveData<MutableList<GeofenceEntry>>()
    val geofenceEntryList: LiveData<MutableList<GeofenceEntry>> = _geofenceEntryList

    private val _receivedGeofenceData = MutableLiveData<ReceivedGeofenceData>()
    val receivedGeofenceData: LiveData<ReceivedGeofenceData> = _receivedGeofenceData

    init {
        _geofenceList.value = mutableListOf()
        _geofenceEntryList.value = mutableListOf()
    }

    fun addGeofenceIfNotAdded(geofenceEntry: GeofenceEntry) {
        _geofenceEntryList.value?.let {
            if (it.contains(geofenceEntry).not()) {
                Log.d(LocationFragment.TAG, "XLOCX - addGeofenceIfNotAdded: will add entry")
                it.add(geofenceEntry)
            }
        }
    }

    fun clearGeofenceList() {
        _geofenceList.value?.clear()
    }

    fun clearGeofenceEntryList() {
        _geofenceEntryList.value?.clear()
    }

    fun addGeofencingEntries() {
        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Home", 34.1602, -118.7012
            )
        )

        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Gym", 34.1457, -118.7668
            )
        )

        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Moms", "34:8:48.30", "-118:23:46.84"
            )
        )


        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Starbucks", 34.1443046567, -118.700355766
            )
        )

        clearGeofenceList()
    }

    override fun onReceive(deviceAction: DeviceAction, transitionGeofences: List<Geofence>) {
        _receivedGeofenceData.postValue(ReceivedGeofenceData(deviceAction, transitionGeofences))
    }

    fun addLocationObserver(deviceService: DeviceService) {
        _deviceService.postValue(deviceService)
        _deviceService.value?.addLocationObserver(this)
    }

    private fun removeLocationObserver() {
        _deviceService.value?.let {
            it.removeLocationObserver(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeLocationObserver()
    }

}