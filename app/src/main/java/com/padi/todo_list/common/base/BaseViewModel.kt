package com.padi.todo_list.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.padi.todo_list.common.SingleLiveEvent
import com.padi.todo_list.common.extension.add
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class BaseViewModel: ViewModel() {
    val subscriptions: CompositeDisposable by lazy { CompositeDisposable() }

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val _errorId = SingleLiveEvent<Int>()
    val errorId: LiveData<Int> = _errorId

    private val _showLoadingEvent = MutableLiveData<Boolean>()
    val showLoadingEvent: LiveData<Boolean>
        get() = _showLoadingEvent

    fun showLoading() {
        _showLoadingEvent.value = true
    }

    fun hideLoading() {
        _showLoadingEvent.value = false
    }


    override fun onCleared() {
        super.onCleared()
        subscriptions.dispose()
    }

    fun <R1 : Any, R2 : Any> fetchZip2Data(
        s1: Maybe<R1>,
        s2: Maybe<R2>,
        showLoading: Boolean = true,
        onSuccess: (r1: R1, r2: R2) -> Unit,
    ) {
        Maybe.zip(s1, s2) { t1, t2 -> Pair(t1, t2) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { if (showLoading) showLoading() }
            .doFinally { hideLoading() }
            .subscribe {
                onSuccess.invoke(it.first, it.second)
            }.add(subscriptions)
    }
}