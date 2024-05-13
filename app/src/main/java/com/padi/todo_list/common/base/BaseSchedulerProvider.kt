package com.padi.todo_list.common.base

import io.reactivex.rxjava3.core.Scheduler

interface BaseSchedulerProvider {
    fun computation(): Scheduler
    fun io(): Scheduler
    fun ui(): Scheduler
}