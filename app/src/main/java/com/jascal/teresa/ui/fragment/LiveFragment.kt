package com.jascal.teresa.ui.fragment

import android.os.Bundle
import com.jascal.teresa.R
import com.jascal.teresa.base.BaseFragment

/**
 * @author jascal
 * @time 2018/6/29
 * describe
 */
class LiveFragment : BaseFragment() {
    override fun lazyLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): LiveFragment {
            val fragment = LiveFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_live

    override fun initView() {
    }

    init {

    }
}