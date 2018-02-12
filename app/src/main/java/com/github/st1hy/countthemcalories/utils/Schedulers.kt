package com.github.st1hy.countthemcalories.utils

import com.github.st1hy.countthemcalories.contract.Schedulers
import com.github.st1hy.countthemcalories.contract.SchedulersProvider
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchedulersProviderImp @Inject constructor() : SchedulersProvider {

    private var hook: Schedulers = DEFAULT

    override fun io(): Scheduler {
        return hook.io()
    }

    override fun computation(): Scheduler {
        return hook.computation()
    }

    override fun ui(): Scheduler {
        return hook.ui()
    }

    override fun registerOverride(schedulers: Schedulers) {
        hook = schedulers
    }

    override fun reset() {
        hook = DEFAULT
    }
}

private val DEFAULT = HookImp()

class HookImp : Schedulers {
    override fun io(): Scheduler {
        return rx.schedulers.Schedulers.io()
    }

    override fun computation(): Scheduler {
        return rx.schedulers.Schedulers.computation()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}