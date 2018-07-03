package com.jascal.teresa.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.jascal.teresa.MyApplication

/**
 * @author jascal
 * @time 2018/6/28
 * describe baseActivity of Teresa
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutID())
        initData()
        initView()
    }

    abstract fun layoutID(): Int

    abstract fun initData()

    abstract fun initView()

    // memory watcher
    override fun onDestroy() {
        super.onDestroy()
        MyApplication.getRefWatcher(this)?.watch(this)
    }

}