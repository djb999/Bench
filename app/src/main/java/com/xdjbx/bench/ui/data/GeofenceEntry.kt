package com.xdjbx.bench.ui.data

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class GeofenceEntry(val id: String, var lat: Double, var long: Double) {

    constructor(id: String, latDms: String, longDms: String) : this(
        id,
        Location.convert(latDms),
        Location.convert(longDms)
    )

    var entered = false

    fun retrieveLatLong(): LatLng = LatLng(lat, long)
}