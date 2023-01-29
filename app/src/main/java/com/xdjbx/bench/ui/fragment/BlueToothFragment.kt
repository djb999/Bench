package com.xdjbx.bench.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.xdjbx.bench.domain.DeviceAction
import com.xdjbx.bench.domain.interfaces.BluetoothUpdateObserver
import com.xdjbx.bench.notification.LocalNotificationManager
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.adapter.BluetoothListAdapter
import com.xdjbx.bench.R
import com.xdjbx.bench.viewmodel.BluetoothViewModel
import com.xdjbx.bench.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_blue_tooth.*
import kotlinx.android.synthetic.main.fragment_blue_tooth.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlueToothFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlueToothFragment :  DeviceBaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    protected lateinit var bluetoothViewModel: BluetoothViewModel

    // Declare constants for the permissions request code and the permissions to request

    // Centralize this and renumber
    private val REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1
    private val REQUIRED_BLUETOOTH_PERMISSIONS = mutableListOf<String>()

    // Declare a BluetoothAdapter and a list of BluetoothDevice objects
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var pairedDevices: List<BluetoothDevice> = mutableListOf()

    private var permissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        REQUIRED_BLUETOOTH_PERMISSIONS.add(Manifest.permission.BLUETOOTH)
        REQUIRED_BLUETOOTH_PERMISSIONS.add(Manifest.permission.BLUETOOTH_ADMIN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            REQUIRED_BLUETOOTH_PERMISSIONS.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        bluetoothViewModel = ViewModelProvider(requireActivity()).get(BluetoothViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blue_tooth, container, false).rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasBluetoothPermissions()) {
            // The app has the required Bluetooth permissions
            permissionsGranted = true
            loadBluetoothList()
        } else {
            // The app does not have the required Bluetooth permissions
            // Request the permissions
            requestBluetoothPermissions()
        }

        handleViewModelServiceEvents()
    }


    @SuppressLint("MissingPermission")
    private fun handleViewModelServiceEvents() {
        bluetoothViewModel.serviceReceiveException.observe(
            this.viewLifecycleOwner
        ) { exception ->
            addToAllLogs("bluetooth - onReceiveError - Exception ${exception.message}")
        }

        bluetoothViewModel.receivedBluetoothData.observe(this.viewLifecycleOwner, Observer { bluetoothReceiveData ->

            with(bluetoothReceiveData) {

                addToAllLogs("onReceive - Bluetooth device name ${bluetoothDevice.name} - device action $deviceAction")

                if (bluetoothDevice.name == "LEXUS IS") {
                    when (deviceAction) {
                        DeviceAction.CONNECTED -> {
                            val enterCarText = "Entering car"
                            addToAllLogs("onReceive - Bluetooth event = $enterCarText")

                            LocalNotificationManager(requireContext()).notifyMe(enterCarText)
                        }
                        DeviceAction.DISCONNECTED -> {
                            val takeYourTowel = "Take your towel"
                            LocalNotificationManager(requireContext()).notifyMe(takeYourTowel)
                            addToAllLogs("onReceive - Bluetooth event = $takeYourTowel")
                        }
                        else -> {

                        }
                    }
                }
            }

        })
    }


    // Function to check if the app has the required Bluetooth permissions
    private fun hasBluetoothPermissions(): Boolean {
        return REQUIRED_BLUETOOTH_PERMISSIONS.all { permission ->
            this.activity?.let { context ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                )
            } == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission") // Permission already granted
    fun getBluetoothList(): List<BluetoothDevice> {
        // TODO - fix deprecation
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Get the list of paired devices
        return bluetoothAdapter?.bondedDevices?.toMutableList() ?: mutableListOf()
    }

    fun loadBluetoothList() {
        pairedDevices = getBluetoothList()
        showBluetoothList(pairedDevices)
    }

    // TODO - build in permission check for bluetoothAdapter.bondedDevices below

    @SuppressLint("MissingPermission")
    fun checkBluetoothConnection(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter.isEnabled && bluetoothAdapter.bondedDevices.isNotEmpty()
    }

    private fun showBluetoothList(pairedDevices: List<BluetoothDevice>) {
        this.activity?.let { context ->
            val btAdapter = BluetoothListAdapter(context, pairedDevices)
            bluetoothListView.adapter = btAdapter
        }
    }

    // Function to request the required Bluetooth permissions
    private fun requestBluetoothPermissions() {

        this.activity?.let { activity ->
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.BLUETOOTH
                )
            ) {
// Show a message explaining why the permissions are needed
// (e.g. "These permissions are needed to connect to Bluetooth devices")
            }
            requestPermissions(
                REQUIRED_BLUETOOTH_PERMISSIONS.toTypedArray(), REQUEST_CODE_BLUETOOTH_PERMISSIONS
            )
        }

    }

    override fun onResume() {
        super.onResume()
        if (hasBluetoothPermissions()) {
            deviceSharedViewModel.deviceService.value?.let { bluetoothViewModel.addBluetoothObserver(it) }
        }
    }

    override fun onPause() {
        //
//        if (hasBluetoothPermissions()) {
//            deviceSharedViewModel.deviceService.value?.removeBluetoothObserver(this)
//        }
        super.onPause()
    }

    // Override the onRequestPermissionsResult method to handle the permissions request result
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
// The required Bluetooth permissions have been granted
// (e.g. start a Bluetooth connection)
                permissionsGranted = true
                loadBluetoothList()
            } else {
// The required Bluetooth permissions have not been granted
// (e.g. show a message or disable Bluetooth functionality)
                Toast.makeText(
                    activity,
                    getString(R.string.bluetoothFailMessage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

//    @SuppressLint("MissingPermission")
//    override fun onReceive(deviceAction: DeviceAction, bluetoothDevice: BluetoothDevice) {
//        addToAllLogs("onReceive - Bluetooth device name ${bluetoothDevice.name} - device action $deviceAction")
//
//        if (bluetoothDevice.name == "LEXUS IS") {
//            when (deviceAction) {
//                DeviceAction.CONNECTED -> {
//                    val enterCarText = "Entering car"
//                    addToAllLogs("onReceive - Bluetooth event = $enterCarText")
//
//                    LocalNotificationManager(requireContext()).notifyMe(enterCarText)
//                }
//                DeviceAction.DISCONNECTED -> {
//                    val takeYourTowel = "Take your towel"
//                    LocalNotificationManager(requireContext()).notifyMe(takeYourTowel)
//                    addToAllLogs("onReceive - Bluetooth event = $takeYourTowel")
//                }
//                else -> {
//
//                }
//            }
//        }
//    }
//
//    override fun onReceiveError(exception: Exception) {
//        TODO("Not yet implemented")
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlueToothFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlueToothFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}