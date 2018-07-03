package com.jascal.teresa.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author jascal
 * @time 2018/6/28
 * describe base presenter
 */
open class BasePresenter<T : IView> : IPresenter<T> {
    var mRootView: T? = null
        private set

    private val isViewAttached: Boolean
        get() = mRootView != null

    private var compositeDisposable = CompositeDisposable()

    override fun detachView() {
        mRootView = null
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    override fun attachView(mRootView: T) {
        this.mRootView = mRootView
    }

    fun checkViewAttache() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() :
            RuntimeException("Please call IPresenter.attachView(IBaseView) before"
                    + " requesting data to the IPresenter")

}