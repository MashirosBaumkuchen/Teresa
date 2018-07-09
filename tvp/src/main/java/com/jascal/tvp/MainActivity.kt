package com.jascal.tvp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.jascal.teresa.glide.GlideApp
import com.jascal.tvp.custom.VideoPlayer
import com.jascal.tvp.utils.Logger
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val DEMO_URI: String = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=14914&editionType=default&source=ucloud"
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set cover
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideApp.with(this)
                .load(VideoPlayer.DEMO_COVER)
                .centerCrop()
                .into(imageView)
        mViewPlayer.setCover(imageView)
    }

    override fun onPause() {
        super.onPause()
        Logger.showLog("onPause")
    }

    override fun onResume() {
        super.onResume()
        Logger.showLog("onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.showLog("onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Logger.showLog("onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        Logger.showLog("onRestoreInstanceState")
    }


}
