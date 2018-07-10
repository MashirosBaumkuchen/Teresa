package com.jascal.tvp.utils

import android.util.Log

class Logger{
    companion object {
        fun showLog(message:String){
            Log.d("Gesture", message)
        }
    }
}