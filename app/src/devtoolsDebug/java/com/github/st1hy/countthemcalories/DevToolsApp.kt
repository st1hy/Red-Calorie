package com.github.st1hy.countthemcalories

import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

class DevToolsApp : DebugApp() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        Timber.d("Starting")
        LeakCanary.install(this)
        Timber.d("Finished devtools setup")
    }

}