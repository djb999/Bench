package com.xdjbx.bench.core

import android.app.PendingIntent
import android.os.Build

object VersionUtil {
    fun retrievePendingIntentMutabilityFlag(mutable: Boolean) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                (if (mutable) PendingIntent.FLAG_MUTABLE else
                                PendingIntent.FLAG_IMMUTABLE) else 0

}