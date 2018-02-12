package com.github.st1hy.countthemcalories.ui.rules

import android.Manifest
import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.support.test.InstrumentationRegistry
import android.support.test.runner.permission.PermissionRequester
import com.github.st1hy.countthemcalories.ui.contract.AppComponentProvider
import com.github.st1hy.countthemcalories.ui.inject.AppTestComponent
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationModule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ApplicationComponentRule(context: Context) : TestRule {

    private val application: Application = context.applicationContext as Application
    private val contentProvider: AppComponentProvider = application as AppComponentProvider

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                setup()
                base.evaluate()
            }
        }
    }

    private fun setup() {
        //AndroidJUnit4 seems to be leaking activities by itself
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)

        contentProvider.component = DaggerApplicationTestComponent.builder()
                .applicationModule(ApplicationModule(application))
                .build()

        val permissionRequester = PermissionRequester()
        permissionRequester.addPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionRequester.requestPermissions()
    }
}

fun getTestComponent() {
    val provider = InstrumentationRegistry.getTargetContext().applicationContext as AppComponentProvider
    val component = provider.component as AppTestComponent
}