package com.xdjbx.bench.processor

import com.google.android.gms.location.Geofence

object GeofenceProcessor {

    fun isGeofenceEntered(geofenceTransition: Int): Boolean {
        return geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
    }

    fun isGeofenceExited(geofenceTransition: Int): Boolean {
        return geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
    }

    fun isGeofenceTransitioned(geofenceTransition: Int): Boolean {
        return isGeofenceEntered(geofenceTransition) || isGeofenceExited(geofenceTransition)
    }
}