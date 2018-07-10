package com.jascal.tvp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.jascal.teresa.glide.GlideApp
import com.jascal.tvp.utils.Logger
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {

    companion object {
        const val DEMO_URI: String = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=113514&resourceType=video&editionType=default&source=aliyun"
        const val DEMO_COVER: String = "http://img.kaiyanapp.com/cbe8ea9ae48ff855ef3d51a16733ede3.png?imageMogr2/quality/60/format/jpg"
    }


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set cover
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideApp.with(this)
                .load(DEMO_COVER)
                .centerCrop()
                .into(imageView)
        mViewPlayer.setCover(imageView)

        // set uri
        mViewPlayer.setData(DEMO_URI)
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
