package com.jascal.teresa.ui.adapter

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
interface MultipleType<in T> {
    fun getLayoutId(item: T, position: Int): Int
}
