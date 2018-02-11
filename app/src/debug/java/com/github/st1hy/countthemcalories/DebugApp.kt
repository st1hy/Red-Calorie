package com.github.st1hy.countthemcalories

import android.os.StrictMode
import timber.log.Timber

open class DebugApp : App() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
    }
}
