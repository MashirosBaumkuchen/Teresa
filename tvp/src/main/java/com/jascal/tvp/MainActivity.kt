package com.jascal.tvp

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DEMO_URI: String = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=14914&editionType=default&source=ucloud"
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
