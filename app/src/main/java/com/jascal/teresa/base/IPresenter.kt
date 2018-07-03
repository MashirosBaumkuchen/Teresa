package com.jascal.teresa.base

/**
 * @author jascal
 * @time 2018/6/28
 * describe base interface of presenter
 */
interface IPresenter<in V : IView> {
    fun attachView(mRootView: V)

    fun detachView()
}