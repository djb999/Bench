package com.xdjbx.bench.ui.activity

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.adapter.BenchPagerAdapter
import com.xdjbx.bench.ui.fragment.*

// Replace package name - com.xdjbx.sensors --> com.xdjbx.bench.ui
import com.xdjbx.sensors.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var broadcastReceiver: GeneralBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPagerMain

        val benchPagerAdapter = BenchPagerAdapter(supportFragmentManager, lifecycle)
        benchPagerAdapter.addFragment(DiagnosticsFragment())
        benchPagerAdapter.addFragment(LocationFragment())
        benchPagerAdapter.addFragment(BlueToothFragment())
        benchPagerAdapter.addFragment(WifiFragment())
        benchPagerAdapter.addFragment(SensorsFragment())

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = benchPagerAdapter

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // TODO - move intent registration and monitoring to a service
        registerIntents()

    }

    // TODO - move intent registration and monitoring to a service
    fun registerIntents() {
        // Initialize the broadcast receiver
        broadcastReceiver = GeneralBroadcastReceiver

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        // Register the broadcast receiver
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        // TODO - move to service
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    private fun checkForPlayServices() {

        // TODO - check play services

//        val response: Int =
//            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)
//        if (response != ConnectionResult.SUCCESS) {
//            Log.d(TAG, "Google Play Service Not Available")
//            GoogleApiAvailability.getInstance().getErrorDialog(this@MainActivity, response, 1)
//                .show()
//        } else {
//            Log.d(TAG, "Google play service available")
//        }
    }
}