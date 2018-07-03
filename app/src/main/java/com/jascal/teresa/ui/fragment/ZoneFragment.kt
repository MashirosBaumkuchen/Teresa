package com.jascal.teresa.ui.fragment

import android.os.Bundle
import com.jascal.teresa.R
import com.jascal.teresa.base.BaseFragment
import com.jascal.teresa.mvp.model.DiscoverModel

/**
 * @author jascal
 * @time 2018/6/29
 * describe
 */
class ZoneFragment : BaseFragment() {
    override fun lazyLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mTitle: String? = null

    companion object {
        fun getInstance(title: String): ZoneFragment {
            val fragment = ZoneFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    init {

    }

    override fun getLayoutId(): Int = R.layout.fragment_zone

    override fun initView() {
    }
}