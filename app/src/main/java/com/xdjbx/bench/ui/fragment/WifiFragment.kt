package com.xdjbx.bench.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.WifiUpdateObserver
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.adapter.WifiConnectionAdapter
import com.xdjbx.bench.ui.data.WifiConnection
import com.xdjbx.sensors.R
import kotlinx.android.synthetic.main.fragment_wifi_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A fragment representing a list of Items.
 */
class WifiFragment : Fragment(), WifiUpdateObserver {

    private var availableNetworks: MutableList<ScanResult> = mutableListOf()

    // Declare constants for the permissions request code and the permissions to request
    private val REQUEST_CODE_WIFI_PERMISSIONS = 2
    private val REQUIRED_WIFI_PERMISSIONS = mutableListOf<String>()
    private lateinit var wifiManager: WifiManager
    private var permissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permissionsRequiredByVersion()) {
            REQUIRED_WIFI_PERMISSIONS.add(Manifest.permission.ACCESS_WIFI_STATE)
            REQUIRED_WIFI_PERMISSIONS.add(Manifest.permission.CHANGE_WIFI_STATE)
            REQUIRED_WIFI_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            REQUIRED_WIFI_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        wifiManager = ((activity as Context).getSystemService(Context.WIFI_SERVICE) as WifiManager)
    }

    private fun permissionsRequiredByVersion() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi_list, container, false).rootView
    }

    override fun onResume() {
        super.onResume()

        if (hasWifiPermissions()) {
            // The app has the required Bluetooth permissions
            permissionsGranted = true
            GeneralBroadcastReceiver.addWifiObserver(this)
            startScanRetrieveResultsAndShow()
        } else if (permissionsRequiredByVersion()) {
            // The app does not have the required Bluetooth permissions
            // Request the permissions
            requestWifiPermissions()
        }
    }

    private fun startScanRetrieveResultsAndShow() {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "XWFIX - startScanRetrieveResultsAndShow: executing startScan")
            startScan()
            availableNetworks = retrieveWifiList()
            Log.d(TAG, "XWFIX - startScanRetrieveResultsAndShow network list size: ${availableNetworks.size}")

            withContext(Dispatchers.Main) {
                Log.d(TAG, "XWFIX - onResume: executing showWifiList")
                showWifiList(availableNetworks)
            }
        }
    }

    override fun onPause() {
        GeneralBroadcastReceiver.removeWifiObserver(this)
        super.onPause()
    }

    // Function to request the required Bluetooth permissions
    private fun requestWifiPermissions() {

        this.activity?.let { activity ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.BLUETOOTH
                )
            ) {
                // TODO - add code here

// Show a message explaining why the permissions are needed
// (e.g. "These permissions are needed to scan for wifi devices")
            }
            ActivityCompat.requestPermissions(
                activity,
                REQUIRED_WIFI_PERMISSIONS.toTypedArray(), REQUEST_CODE_WIFI_PERMISSIONS
            )
        }
    }

    // Function to check if the app has the required Bluetooth permissions
    private fun hasWifiPermissions(): Boolean {
        return REQUIRED_WIFI_PERMISSIONS.all { permission ->
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
        if (requestCode == REQUEST_CODE_WIFI_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
// The required WiFi permissions have been granted
// (e.g. start a WiFi connection)
                permissionsGranted = true
                startScanRetrieveResultsAndShow()
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
    }


    private fun retrieveWifiList(): MutableList<ScanResult> {
        return wifiManager.scanResults.filter { !it.SSID.isEmpty() }.toMutableList()
    }

    private fun showWifiList(networkList: MutableList<ScanResult>) {
        val wifiList = ArrayList<WifiConnection>()
        networkList.forEach { network ->
            Log.d(TAG, "XWFIX - showWifiList - adding to list ${network.SSID}")

            wifiList.add(WifiConnection(network.SSID, false))
        }

        val adapter = WifiConnectionAdapter(wifiList)
        wifiListRecyclerView.adapter = adapter
    }

    private fun startScan() {
        wifiManager.startScan()
    }

    override fun onReceive(deviceAction: DeviceAction, scanResults: List<ScanResult>) {
        startScanRetrieveResultsAndShow()
    }

    companion object {
        val TAG = "WifiFragment"
    }
}