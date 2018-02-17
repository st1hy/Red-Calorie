package com.github.st1hy.countthemcalories.inject

import com.github.st1hy.countthemcalories.contract.Schedulers
import com.github.st1hy.countthemcalories.ui.contract.SchedulersProvider
import com.github.st1hy.countthemcalories.utils.SchedulersProviderImp
import dagger.Binds
import dagger.Module

@Module
abstract class SchedulersModule {

    @Binds abstract fun schedulersProvider(provider: SchedulersProviderImp) : SchedulersProvider

    @Binds abstract fun schedulers(provider: SchedulersProviderImp) : Schedulers
}