package com.xdjbx.bench.ui.activity

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.xdjbx.bench.ui.GeneralBroadcastReceiver
import com.xdjbx.bench.ui.adapter.BenchPagerAdapter
import com.xdjbx.bench.ui.fragment.BlueToothFragment
import com.xdjbx.bench.ui.fragment.SensorsFragment

// Replace package name - com.xdjbx.sensors --> com.xdjbx.bench.ui
import com.xdjbx.sensors.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var broadcastReceiver: GeneralBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationIntent = Intent(this, MainActivity::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPagerMain

        val benchPagerAdapter = BenchPagerAdapter(supportFragmentManager, lifecycle)
        benchPagerAdapter.addFragment(SensorsFragment())
        benchPagerAdapter.addFragment(BlueToothFragment())

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = benchPagerAdapter

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // TODO - move intent registration and monitoring to a service
        registerIntents()

    }

    // TODO - move intent registration and monitoring to a service
    fun registerIntents() {
        // Initialize the broadcast receiver
        broadcastReceiver = GeneralBroadcastReceiver()

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)

        // Register the broadcast receiver
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onDestroy() {
        // TODO - move to service
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}