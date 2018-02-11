package com.github.st1hy.countthemcalories.ui.core.baseview

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.github.st1hy.countthemcalories.ui.contract.AppComponent
import com.github.st1hy.countthemcalories.ui.contract.AppComponentProvider

abstract class BaseActivity : AppCompatActivity() {

    fun getAppComponent() : AppComponent = (application as AppComponentProvider).component

}

abstract class BaseFragment : Fragment()