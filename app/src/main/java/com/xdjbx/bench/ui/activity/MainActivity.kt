package com.xdjbx.bench.ui.activity

// Replace package name - com.xdjbx.bench --> com.xdjbx.bench.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.xdjbx.bench.ui.adapter.BenchPagerAdapter
import com.xdjbx.bench.ui.fragment.*
import com.xdjbx.bench.ui.interfaces.IBaseFragment
import com.xdjbx.bench.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val baseFragmentList = mutableListOf<IBaseFragment>()

//    private lateinit var broadcastReceiver: GeneralBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPagerMain

        val benchPagerAdapter = BenchPagerAdapter(supportFragmentManager, lifecycle)
        benchPagerAdapter.addFragment(DiagnosticsFragment())

        addFragmentAndBase(LocationFragment(), benchPagerAdapter)

        benchPagerAdapter.addFragment(BlueToothFragment())

        addFragmentAndBase(WifiFragment(), benchPagerAdapter)
        benchPagerAdapter.addFragment(SensorsFragment())

        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.adapter = benchPagerAdapter

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // TODO - move intent registration and monitoring to a service
//        registerIntents()

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                baseFragmentList.forEach { it.resetPermissionCheckingInProcess() }
            }
        })

    }

    private fun addFragmentAndBase(
        iBaseFragment: IBaseFragment,
        benchPagerAdapter: BenchPagerAdapter
    ) {
        baseFragmentList.add(iBaseFragment)
        benchPagerAdapter.addFragment(iBaseFragment as Fragment)
    }

    // TODO - move intent registration and monitoring to a service
//    fun registerIntents() {
//        // Initialize the broadcast receiver
//        broadcastReceiver = GeneralBroadcastReceiver
//
//        val filter = IntentFilter()
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
//
//        // Register the broadcast receiver
//        registerReceiver(broadcastReceiver, filter)
//    }

//    override fun onDestroy() {
//        // TODO - move to service
////        unregisterReceiver(broadcastReceiver)
//        super.onDestroy()
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//    }

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