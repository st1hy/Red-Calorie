package com.github.st1hy.countthemcalories.inject

import com.github.st1hy.countthemcalories.App
import com.github.st1hy.countthemcalories.ui.contract.AppComponent
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        ApplicationModule::class,
        DatabaseRepoModule::class
))
interface AppComponent : AppComponent {

    fun inject(application: App)

}