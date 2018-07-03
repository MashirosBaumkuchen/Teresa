package com.jascal.teresa.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jascal.teresa.MyApplication

/**
 * @author jascal
 * @time 2018/6/28
 * describe baseFragment of Teresa
 */
abstract class BaseFragment : Fragment() {
    private var isViewPrepare = false
    private var hasLoadData = false

    // kotlin: onCreateView return view, then onViewCreated will init these views
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getLayoutId(), null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        lazyLoadDataIfPrepared()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun initView()

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    abstract fun lazyLoad()

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.getRefWatcher(activity)?.watch(activity)
    }
}