package com.xdjbx.bench.application

import android.app.Application
import android.os.StrictMode

class BenchApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO - reenable Strictmode
        // TODO - add leak canary
//        StrictMode.enableDefaults()
    }
}