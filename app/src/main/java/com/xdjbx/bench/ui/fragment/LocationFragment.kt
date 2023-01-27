package com.xdjbx.bench.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.LocationUpdateObserver
import com.xdjbx.bench.notification.LocalNotificationManager
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.adapter.LocationEntryAdapter
import com.xdjbx.bench.ui.data.GeofenceEntry
import com.xdjbx.bench.ui.interfaces.IBaseFragment
import com.xdjbx.bench.R
import kotlinx.android.synthetic.main.fragment_location_list.*


/**
 * A fragment representing a list of Items.
 */
class LocationFragment : DeviceBaseFragment(), IBaseFragment, LocationUpdateObserver {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // TODO - move to viewModel
    private val geofenceList: MutableList<Geofence> = mutableListOf()
    private val geofenceEntryList: MutableList<GeofenceEntry> = mutableListOf()
    lateinit var geofencingClient: GeofencingClient

    // Declare constants for the permissions request code and the permissions to request

    // Centralize this and renumber
    private val REQUEST_CODE_LOCATION_PERMISSIONS = 3
    private val REQUIRED_LOCATION_PERMISSIONS = mutableListOf<String>()

    private var permissionsGranted = false
    private var checkingPermissions = false
    private var locationStartedUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "XLOCX - onCreate - entering")

        REQUIRED_LOCATION_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        REQUIRED_LOCATION_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionsRequiredByVersion()) {
            REQUIRED_LOCATION_PERMISSIONS.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationCoordTextView.text = locationResult.locations[0].let { location ->
                    "${"%.4f".format(location.latitude)}, ${"%.4f".format(location.longitude)}"
                }
            }
        }
    }

    private fun permissionsRequiredByVersion() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "XLOCX - onCreateView - entering")
        return inflater.inflate(R.layout.fragment_location_list, container, false).rootView
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSimulateGeofence.setOnClickListener {
            simulateGeofenceEvent()
        }

        buttonStartLocation.setOnClickListener {
            if (locationStartedUpdates) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                locationStartedUpdates = false
                locationCoordTextView.text = ""
                buttonStartLocation.text = "Start Location"
            } else {
                if (permissionsGranted) {
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    locationStartedUpdates = true
                    buttonStartLocation.text = "Stop Location"

                } else {

                    // TODO - request permissions
//                    if (hasLocationPermissions()) {
//
//                    } else if (permissionsRequiredByVersion()) {
//                        // The app does not have the required Bluetooth permissions
//                        // Request the permissions
//                        requestLocationPermissions()
//                    }
                }
            }
        }
    }


    private fun simulateGeofenceEvent() {

//        val intent = Intent(Constants.GEOFENCE_TRANSITION_ACTION)
//        val transition = Geofence.GEOFENCE_TRANSITION_ENTER // or GEOFENCE_TRANSITION_EXIT
//        val geofenceId = "myGeofenceId"
//        val transitionData = arrayListOf(geofenceId)
//        intent.putExtra(EXTRA_GEOFENCE_TRANSITION_TYPE, transitionData)

        val intentSender = geofencePendingIntent.intentSender

// Send the intent to the service
        intentSender.sendIntent(requireActivity(), 0, Intent(), null, null)


//        val intent = Intent(Constants.GEOFENCE_TRANSITION_ACTION)
//        val bundle = Bundle()
//        bundle.putString("transition", "ENTER") // or "EXIT"
//        bundle.putString("geofenceId", "myGeofenceId")
//        intent.putExtra(GEOFENCE_TRANSITION_EXTRA, Geofence.GEOFENCE_TRANSITION_ENTER)
//        val geofenceList: ArrayList<Geofence> = geofenceList.toJava(ArrayList())
//        intent.putParcelableArrayListExtra(GEOFENCE_TRANSITION_LIST_EXTRA, ArrayList(geofenceList))

//        intent.putExtra("event", bundle)

//        GeneralBroadcastReceiver.onReceive(requireActivity(), intent)
    }

    private fun processPermissions() {
        if (permissionsGranted) {
            addGeofencingEntries()
        } else {
            if (hasLocationPermissions()) {

                geofencingClient = LocationServices.getGeofencingClient(requireActivity())
                // The app has the required Bluetooth permissions
                permissionsGranted = true
                deviceSharedViewModel.deviceService.value?.addLocationObserver(this)
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
        Log.d(TAG, "XLOCX - onResume - entering")
        if (!checkingPermissions) {
            processPermissions()
        } else {
            checkingPermissions = false
        }

    }

    override fun resetPermissionCheckingInProcess() {
        Log.d(TAG, "XLOCX - LocationFragment - resetPermissionCheckingInProcess - entering")
        checkingPermissions = false
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
                    Log.e(
                        TAG,
                        "XLOCX - addGeofencingEntries - geofencingClient.addGeofences() - addOnFailureListener",
                        it
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
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0)
        PendingIntent.getBroadcast(requireActivity(), 0, intent, flags)
    }

    override fun onPause() {
        Log.d(TAG, "XLOCX - onPause - entering")
        deviceSharedViewModel.deviceService.value?.removeLocationObserver(this)
        super.onPause()
    }

    // Function to request the required Bluetooth permissions
    private fun requestLocationPermissions() {

        Log.d(TAG, "XLOCX - requestLocationPermissions - entering")

        // TODO - change permissions and implement
        requireActivity().let { activity ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // TODO - add code here

// Show a message explaining why the permissions are needed
// (e.g. "These permissions are needed to scan for wifi devices")
            }

            Log.d(
                TAG,
                "XLOCX - requestLocationPermissions - checkingPermissions: $checkingPermissions"
            )

            checkingPermissions = true
            Log.d(
                TAG,
                "XLOCX - requestLocationPermissions - calling ActivityCompat.requestPermissions"
            )

            requestPermissions(
                REQUIRED_LOCATION_PERMISSIONS.toTypedArray(), REQUEST_CODE_LOCATION_PERMISSIONS
            )

        }
    }

    // Function to check if the app has the required Bluetooth permissions
    private fun hasLocationPermissions(): Boolean {
        return REQUIRED_LOCATION_PERMISSIONS.all { permission ->
            this.activity?.let { context ->
                ContextCompat.checkSelfPermission(
                    context, permission
                )
            } == PackageManager.PERMISSION_GRANTED
        }
    }

    // Override the onRequestPermissionsResult method to handle the permissions request result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        Log.d(TAG, "XLOCX - entering onRequestPermissionsResult - request Code: $requestCode")

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            Log.d(TAG, "XLOCX - onRequestPermissionsResult - checking all granted ")

            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
// The required WiFi permissions have been granted
// (e.g. start a WiFi connection)
                permissionsGranted = true
                addGeofencingEntries()
            } else {
// The required WiFi permissions have not been granted
// (e.g. show a message or disable WiFi functionality)

                val deniedPermissions = mutableListOf<String>()
                grantResults.forEachIndexed { index, result ->
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[index])
                    }
                }
                if (deniedPermissions.isNotEmpty()) {
                    deniedPermissions.forEach { permission ->
                        addToAllLogs("onRequestPermissionsResult - permission int: ${permission} failed to grant")
                    }
                }


                // TODO - if the background location permission is not granted - requestPermissions does not
                // TODO - launch the dialog. We will need to ask the user with the shouldShowRequestPermissionRationale
                // TODO - to select 'Allow all the time' instead of 'Allow while using the app' otherwise it geofencing calls
                // TODO - will fail.


//                if (permissionsRequiredByVersion()) {
//                    if (deniedPermissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//                        val permissions = arrayOf(Manifest.permission.CAMERA)
//                        requestPermissions(
//                            permissions,
//                            requestCode,
//                            PackageManager.PERMISSION_GRANTED_AFTER_REQUEST
//                        )
//                    }
//                }


                Toast.makeText(
                    activity, getString(R.string.locationFailMessage), Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Log.d(TAG, "XLOCX - onRequestPermissionsResult - request code didn't match")

            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun addGeofenceEntry(geofenceEntry: GeofenceEntry): Boolean {

        // TODO move to constants
        // TODO - make property dynamic
        val GEOFENCE_RADIUS_IN_METERS = 20F
        val GEOFENCE_EXPIRATION_IN_MILLISECONDS = 3600 * 1000L

        val latLng = geofenceEntry.retrieveLatLong()

        val geofence = Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(geofenceEntry.id)

            // Set the circular region of this geofence.
            .setCircularRegion(
                latLng.latitude, latLng.longitude, GEOFENCE_RADIUS_IN_METERS
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

    override fun onReceiveError(exception: Exception) {
        addToAllLogs("onReceiveError - Exception ${exception.message}")
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

    override fun onStop() {
        if (locationStartedUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            locationStartedUpdates = false
        }
        super.onStop()
    }

    companion object {
        val TAG = "LocationFragment"
    }
}