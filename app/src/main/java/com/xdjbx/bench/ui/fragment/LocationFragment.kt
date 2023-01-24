package com.xdjbx.bench.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.LocationUpdateObserver
import com.xdjbx.bench.notification.LocalNotificationManager
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.adapter.LocationEntryAdapter
import com.xdjbx.bench.ui.data.GeofenceEntry
import com.xdjbx.sensors.R
import kotlinx.android.synthetic.main.fragment_location_list.*

/**
 * A fragment representing a list of Items.
 */
class LocationFragment : Fragment(), LocationUpdateObserver {

    // TODO - move to viewModel
    private val geofenceList: MutableList<Geofence> = mutableListOf()
    private val geofenceEntryList: MutableList<GeofenceEntry> = mutableListOf()
    lateinit var geofencingClient: GeofencingClient
    private val eventList: MutableList<String> = mutableListOf()

    // Declare constants for the permissions request code and the permissions to request

    // Centralize this and renumber
    private val REQUEST_CODE_LOCATION_PERMISSIONS = 3
    private val REQUIRED_LOCATION_PERMISSIONS = mutableListOf<String>()

    private var permissionsGranted = false
    private var checkingPermissions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permissionsRequiredByVersion()) {
            REQUIRED_LOCATION_PERMISSIONS.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            REQUIRED_LOCATION_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }

    private fun permissionsRequiredByVersion() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_list, container, false).rootView
    }

    private fun processPermissions() {
        if (permissionsGranted) {
            addGeofencingEntries()
        } else {
            if (hasLocationPermissions()) {

                geofencingClient = LocationServices.getGeofencingClient(requireActivity())
                // The app has the required Bluetooth permissions
                permissionsGranted = true
                GeneralBroadcastReceiver.addLocationObserver(this)
                addGeofencingEntries()
            } else if (permissionsRequiredByVersion()) {
                // The app does not have the required Bluetooth permissions
                // Request the permissions
                requestLocationPermissions()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        processPermissions()
    }

    private fun addGeofenceIfNotAdded(geofenceEntry: GeofenceEntry) {
        if (geofenceEntryList.contains(geofenceEntry).not()) {
            Log.d(TAG, "XLOCX - addGeofenceIfNotAdded: will add entry")

            geofenceEntryList.add(geofenceEntry)
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofencingEntries() {

//        GlobalScope.launch(Dispatchers.IO) {
        Log.d(TAG, "XLOCX - addGeofencingEntries: adding entries")

        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Home",
                "34:9:36.41",
                "-118:42:4.70"
            )
        )

        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Gym",
                "34:8:42.94",
                "-118:46:1.79"
            )
        )

        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Moms",
                "34:8:48.30",
                "-118:23:46.84"
            )
        )


        addGeofenceIfNotAdded(
            GeofenceEntry(
                "Starbucks",
                34.1443046567, -118.700355766
            )
        )

        geofenceList.clear()
        geofenceEntryList.forEach { addGeofenceEntry(it) }

        if (geofenceList.isNotEmpty()) {
            Log.d(TAG, "XLOCX - addGeofencingEntries - building geofence request")

            val geofencingRequest = retrieveGeofenceRequest()

            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.d(
                        TAG,
                        "XLOCX - addGeofencingEntries - geofencingClient.addGeofences() - addSuccessListener"
                    )

                    // Geofences added
                    // ...
                }
                addOnFailureListener {
                    Log.d(
                        TAG,
                        "XLOCX - addGeofencingEntries - geofencingClient.addGeofences() - addOnFailureListener"
                    )
                    // Failed to add geofences
                    // ...

                }
            }


        }

//            withContext(Dispatchers.Main) {
        Log.d(TAG, "XLOCX - onResume: will show geofencing entries soon")
        showLocationList(geofenceEntryList)
//            }
//        }
    }

    private fun retrieveGeofenceRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireActivity(), GeneralBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().

        val flags =
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0)
        PendingIntent.getBroadcast(requireActivity(), 0, intent, flags)
    }

    @SuppressLint("SetTextI18n")
    fun addEventLogEntry(eventText: String) {
        eventList.add(eventText)
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_list_item_1,
            eventList.toTypedArray()
        )
        event_list_view.adapter = adapter
    }

    override fun onPause() {
        GeneralBroadcastReceiver.removeLocationObserver(this)
        super.onPause()
    }

    // Function to request the required Bluetooth permissions
    private fun requestLocationPermissions() {

        // TODO - change permissions and implement
        requireActivity().let { activity ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // TODO - add code here

// Show a message explaining why the permissions are needed
// (e.g. "These permissions are needed to scan for wifi devices")
            }

            if (!checkingPermissions) {
                checkingPermissions = true
                ActivityCompat.requestPermissions(
                    activity,
                    REQUIRED_LOCATION_PERMISSIONS.toTypedArray(), REQUEST_CODE_LOCATION_PERMISSIONS
                )
            }

        }
    }

    // Function to check if the app has the required Bluetooth permissions
    private fun hasLocationPermissions(): Boolean {
        return REQUIRED_LOCATION_PERMISSIONS.all { permission ->
            this.activity?.let { context ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                )
            } == PackageManager.PERMISSION_GRANTED
        }
    }

    // Override the onRequestPermissionsResult method to handle the permissions request result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
// The required WiFi permissions have been granted
// (e.g. start a WiFi connection)
                permissionsGranted = true
                addGeofencingEntries()
            } else {
// The required WiFi permissions have not been granted
// (e.g. show a message or disable WiFi functionality)
                Toast.makeText(
                    activity,
                    getString(R.string.wifiFailMessage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        checkingPermissions = false
    }

    private fun addGeofenceEntry(geofenceEntry: GeofenceEntry): Boolean {

        // TODO move to constants
        // TODO - make property dynamic
        val GEOFENCE_RADIUS_IN_METERS = 20F
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS = 3600 * 1000L

        val latLng = geofenceEntry.retrieveLatLong()

        val geofence =
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(geofenceEntry.id)

                // Set the circular region of this geofence.
                .setCircularRegion(
                    latLng.latitude,
                    latLng.longitude,
                    GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build()



        if (!geofenceList.contains(element = geofence)) {
            addToAllLogs("addGeofencingEntries - adding entry: ${geofence.requestId}")
            geofenceList.add(geofence)
            return true
        }

        return false
    }

    private fun addToAllLogs(text: String) {
        addEventLogEntry(text)
        Log.d(TAG, "XLOCX - ${text}")
    }

    private fun showLocationList(locationEntryList: MutableList<GeofenceEntry>) {
        val locationList = ArrayList<GeofenceEntry>()
        locationEntryList.forEach { geofenceEntry ->
            Log.d(TAG, "XLOCX - showLocationList - adding to list ${geofenceEntry.id}")
            locationList.add(geofenceEntry)
        }

        val adapter = LocationEntryAdapter(locationList)
        locationListRecyclerView.adapter = adapter
    }

    override fun onReceive(deviceAction: DeviceAction, transitionGeofences: List<Geofence>) {
        when (deviceAction) {
            DeviceAction.ENTERED -> {

                addToAllLogs("onReceive -DeviceAction.ENTERED")

                transitionGeofences.forEach {
                    LocalNotificationManager(requireContext()).notifyMe("Entering ${it.requestId}")
                    addToAllLogs("onReceive - Entering ${it.requestId}")
                    updateGeofenceList(true, transitionGeofences)
                }
            }
            DeviceAction.EXITED -> {
                transitionGeofences.forEach {
                    LocalNotificationManager(requireContext()).notifyMe("Exiting ${it.requestId}")
                    addToAllLogs("onReceive - Exiting ${it.requestId}")
                    updateGeofenceList(false, transitionGeofences)
                }
            }
            else -> {

            }
        }
    }

    private fun updateGeofenceList(entered: Boolean, transitionGeofences: List<Geofence>) {
        transitionGeofences.forEachIndexed { index, geofence ->
            val entry = geofenceEntryList.find { geofence.requestId == it.id }
            entry?.let {
                it.entered = entered
                locationListRecyclerView.adapter?.notifyItemChanged(index)
            }
        }

    }


    companion object {
        val TAG = "LocationFragment"
    }
}