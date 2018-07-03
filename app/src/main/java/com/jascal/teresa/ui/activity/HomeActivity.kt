package com.jascal.teresa.ui.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import com.jascal.teresa.R
import com.jascal.teresa.base.BaseActivity
import com.jascal.teresa.ui.fragment.DiscoverFragment
import com.jascal.teresa.ui.fragment.LiveFragment
import com.jascal.teresa.ui.fragment.ZoneFragment
import com.jascal.teresa.utils.showToast
import kotlinx.android.synthetic.main.activity_home.*

/**
 * @author jascal
 * @time 2018/6/28
 * describe Teresa main activity
 */
class HomeActivity : BaseActivity() {
    private val mTitles = arrayOf("#discover", "#live", "#the one")
    private var mDiscoverFragment: DiscoverFragment? = null
    private var mLiveFragment: LiveFragment? = null
    private var mZoneFragment: ZoneFragment? = null
    private var mIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt("currTabIndex")
        }
        super.onCreate(savedInstanceState)
        switchFragment(mIndex)
    }

    override fun layoutID(): Int = R.layout.activity_home

    override fun initData() {

    }

    override fun initView() {
        for (title in mTitles) {
            var tab = tab_layout.newTab()
            tab.text = title
            tab_layout.addTab(tab)
        }
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                showToast("onTabReselected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                showToast("onTabUnselected")
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                showToast("onTabSelected")
                switchFragment(tab.position)
            }
        })
    }

    private fun switchFragment(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (position) {
            //discover fragment
            0 -> mDiscoverFragment?.let {
                transaction.show(it)
            } ?: DiscoverFragment.getInstance(mTitles[position]).let {
                mDiscoverFragment = it
                transaction.add(R.id.content, it, "#discover")
            }
            // live fragment
            1 -> mLiveFragment?.let {
                transaction.show(it)
            } ?: LiveFragment.getInstance(mTitles[position]).let {
                mLiveFragment = it
                transaction.add(R.id.content, it, "#live")
            }
            // zone fragment
            2 -> mZoneFragment?.let {
                transaction.show(it)
            } ?: ZoneFragment.getInstance(mTitles[position]).let {
                mZoneFragment = it
                transaction.add(R.id.content, it, "#zone")
            }
        }
        mIndex = position
        transaction.commitAllowingStateLoss()
    }c

    private fun hideFragments(transaction: FragmentTransaction) {
        mDiscoverFragment?.let { transaction.hide(it) }
        mLiveFragment?.let { transaction.hide(it) }
        mZoneFragment?.let { transaction.hide(it) }
    }


    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis().minus(mExitTime) <= 2000) {
                finish()
            } else {
                mExitTime = System.currentTimeMillis()
                showToast("再按一次退出程序")
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


}