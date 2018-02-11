package com.github.st1hy.countthemcalories.ui.core.baseview

import android.support.v7.app.AppCompatActivity
import com.github.st1hy.countthemcalories.ui.contract.AppComponent
import com.github.st1hy.countthemcalories.ui.contract.AppComponentProvider

fun AppCompatActivity.getAppComponent() : AppComponent =
        (application as AppComponentProvider).component