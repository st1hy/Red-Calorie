package com.github.st1hy.countthemcalories

import android.annotation.SuppressLint
import android.os.Build
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.github.st1hy.countthemcalories.ui.contract.AppComponent
import com.github.st1hy.countthemcalories.ui.contract.AppComponentProvider
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationModule
import com.github.st1hy.countthemcalories.utils.lazyRW
import net.danlew.android.joda.JodaTimeAndroid
import rx.plugins.RxJavaErrorHandler
import rx.plugins.RxJavaPlugins

@SuppressLint("Registered") // It is registered, but lint looks elsewhere
open class App : MultiDexApplication(), AppComponentProvider {

    override var component: AppComponent by lazyRW {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    @SuppressWarnings("LogNotTimber")
    override fun onCreate() {
        super.onCreate()

        JodaTimeAndroid.init(applicationContext)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            RxJavaPlugins.getInstance().registerErrorHandler(object : RxJavaErrorHandler() {
                override fun handleError(e: Throwable?) {
                    try {
                        Log.e("CRASH", "Unhandled exception", e)
                    } catch (t: Throwable) {
                        //Required by error handler
                    }

                }
            })
        }
    }

}
