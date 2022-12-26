package com.xdjbx.bench.ui.activity

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.xdjbx.bench.ui.adapter.BenchPagerAdapter
import com.xdjbx.bench.ui.fragment.BlueToothFragment
import com.xdjbx.bench.ui.fragment.SensorsFragment
import com.xdjbx.sensors.R
import com.xdjbx.sensors.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPagerMain

        val benchPagerAdapter = BenchPagerAdapter(supportFragmentManager, lifecycle)
        benchPagerAdapter.addFragment(SensorsFragment())
        benchPagerAdapter.addFragment(BlueToothFragment())

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = benchPagerAdapter

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

}