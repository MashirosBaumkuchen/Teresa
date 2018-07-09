package com.jascal.tvp

import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class MyApplication : Application() {
    companion object {
        private val TAG = "MyApplication"
        var context: Context by Delegates.notNull()
            private set
    }
}