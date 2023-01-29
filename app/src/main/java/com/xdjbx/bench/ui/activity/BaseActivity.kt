package com.xdjbx.bench.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.xdjbx.bench.service.DeviceService
import com.xdjbx.bench.viewmodel.DeviceSharedViewModel

open class BaseActivity : AppCompatActivity(), ServiceConnection {

    protected lateinit var deviceSharedViewModel: DeviceSharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO - make the service a foreground service

        deviceSharedViewModel = ViewModelProvider(this)[DeviceSharedViewModel::class.java]

        val intent = Intent(this, DeviceService::class.java)

        // TODO - this will require the FOREGROUND_SERVICE service permission to be requested and means the starting of the
        // TODO - service will need to be deferred so for now leave it unsticky.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        }
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        deviceSharedViewModel.addService((service as DeviceService.DeviceBinder).getService())
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }
}