package com.xdjbx.bench.ui.fragment

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.xdjbx.bench.R
import kotlinx.android.synthetic.main.fragment_sensors.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var square: TextView

/**
 * A simple [Fragment] subclass.
 * Use the [SensorsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SensorsFragment : Fragment(), SensorEventListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var sensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_sensors, container, false)
        val root = layout.rootView

        square = root.tv_square

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSensorStuff()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SensorsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SensorsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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

    private fun setupSensorStuff() {
        sensorManager = activity?.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensorManager?.let { manager ->
            manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).also {
                manager.registerListener(this, it,
                    SensorManager.SENSOR_DELAY_FASTEST,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }
        }
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(this)
        super.onDestroy()
    }
}