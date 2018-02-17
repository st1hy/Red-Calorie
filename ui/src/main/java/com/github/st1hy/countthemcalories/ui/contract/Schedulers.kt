package com.github.st1hy.countthemcalories.ui.contract

import rx.Scheduler

interface Schedulers {
    fun io() : Scheduler
    fun computation() : Scheduler
    fun ui() : Scheduler
}

interface SchedulersProvider : Schedulers {

    fun registerOverride(schedulers: Schedulers)

    fun reset()
}