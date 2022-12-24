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
import com.xdjbx.sensors.R

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        square = findViewById(R.id.tv_square)

        setupSensorStuff()
    }

    private fun setupSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).also {
            sensorManager.registerListener(this, it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val horizontal = event.values[0]
            val vertical = event.values[1]
            val zAxis = event.values[2]

            square.apply {
                rotationX = vertical * 3F
                rotationY = horizontal * 3F
                rotation = -horizontal
                translationX = horizontal * -10
                translationY = vertical * 10
            }

            val color = if (horizontal.toInt() == 0 && vertical.toInt() == 0) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            square.text = "Vertical ${vertical.toInt()}\nHorizontal ${horizontal.toInt()} \n" +
                    "Z-Axis ${zAxis.toInt()} "
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

}