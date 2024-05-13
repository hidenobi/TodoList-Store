package com.padi.todo_list.common.extension

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

fun Disposable.add(s: CompositeDisposable) {
    s.add(this)
}