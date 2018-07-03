package com.jascal.teresa.utils

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import com.jascal.teresa.MyApplication

/**
 * @author jascal
 * @time 2018/6/28
 * describe kotlin extension functions
 */
fun Context.showToast(content: String): Toast {
    val toast = Toast.makeText(MyApplication.context, content, Toast.LENGTH_LONG)
    toast.show()
    return toast
}

fun Fragment.showToast(content: String): Toast {
    val toast = Toast.makeText(this.activity.applicationContext, content, Toast.LENGTH_SHORT)
    toast.show()
    return toast
}

fun durationFormat(duration: Long?): String {
    val minute = duration!! / 60
    val second = duration % 60
    return if (minute <= 9) {
        if (second <= 9) {
            "0$minute' 0$second''"
        } else {
            "0$minute' $second''"
        }
    } else {
        if (second <= 9) {
            "$minute' 0$second''"
        } else {
            "$minute' $second''"
        }
    }
}

// 数据流量格式化
fun Context.dataFormat(total: Long): String {
    var result: String
    var speedReal: Int = (total / (1024)).toInt()
    result = if (speedReal < 512) {
        speedReal.toString() + " KB"
    } else {
        val mSpeed = speedReal / 1024.0
        (Math.round(mSpeed * 100) / 100.0).toString() + " MB"
    }
    return result
}
