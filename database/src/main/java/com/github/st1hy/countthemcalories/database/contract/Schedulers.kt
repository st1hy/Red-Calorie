package com.github.st1hy.countthemcalories.database.contract

import rx.Scheduler

interface Schedulers {
    fun io() : Scheduler
    fun computation() : Scheduler
    fun ui() : Scheduler
}
