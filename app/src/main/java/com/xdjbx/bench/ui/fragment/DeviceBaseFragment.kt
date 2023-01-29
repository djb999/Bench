package com.xdjbx.bench.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xdjbx.bench.viewmodel.DeviceSharedViewModel

open class DeviceBaseFragment: Fragment() {

    protected lateinit var deviceSharedViewModel: DeviceSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceSharedViewModel = ViewModelProvider(requireActivity()).get(DeviceSharedViewModel::class.java)
    }

    @SuppressLint("SetTextI18n")
    fun addEventLogEntry(eventText: String) {
        deviceSharedViewModel.addEvent(eventText)
    }

    fun addToAllLogs(text: String) {
        addEventLogEntry(text)
        Log.d(LocationFragment.TAG, "XLOCX - ${text}")
    }
}