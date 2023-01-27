package com.xdjbx.bench.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.xdjbx.bench.service.DeviceService
import com.xdjbx.bench.ui.viewmodel.DeviceSharedViewModel

open class BaseActivity : AppCompatActivity(), ServiceConnection {

    protected lateinit var deviceSharedViewModel: DeviceSharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO - make the service a foreground service

        deviceSharedViewModel = ViewModelProvider(this)[DeviceSharedViewModel::class.java]

        val intent = Intent(this, DeviceService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    private var deviceService: DeviceService? = null

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as DeviceService.DeviceBinder
        deviceService = binder.getService()
        deviceService?.let {
            deviceSharedViewModel.addService(it)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }
}