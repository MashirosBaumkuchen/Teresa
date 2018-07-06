package com.jascal.tvp

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null

    companion object {
        private const val DEMO_URI: String = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=14914&editionType=default&source=ucloud"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = MediaPlayer().apply {
            setDataSource(applicationContext, Uri.parse(DEMO_URI))
            prepare()
            setOnPreparedListener {
                player!!.start()
                player!!.isLooping = true
            }
            setOnVideoSizeChangedListener { _: MediaPlayer, width: Int, height: Int ->
                mSurfaceView.layoutParams.height = height
                mSurfaceView.layoutParams.width = width
            }
        }

        mSurfaceView!!.holder.apply {
            addCallback(MediaPlayerCallBack())
        }

        mStart.setOnClickListener {
            player?.let {
                if (!it.isPlaying) {
                    it.start()
                }
            }
        }

        mPause.setOnClickListener {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        }

        Toast.makeText(this, "duration is:" + player!!.duration, Toast.LENGTH_LONG).show()
        val date = Date(player!!.duration.toLong())
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        val totalTime = simpleDateFormat.format(date)
        mDuration.text = totalTime

        startProgress()
        mProgress.max = player!!.duration
        mProgress.progress = 0
        mProgress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                player?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekbar: SeekBar?) {
                player?.pause()
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                player?.start()
            }

        })
    }

    private fun startProgress() {
        //开辟新的Thread用于定期刷新SeekBar;
        val dThread = DelayThread(100)
        dThread.start()
    }

    //开启一个线程进行实时刷新
    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            player?.let {
                mProgress.progress = (it.currentPosition)
            }
        }
    }

    inner class DelayThread(private var milliseconds: Int) : Thread() {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(milliseconds.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                handler.sendEmptyMessage(0)
            }
        }
    }


    private inner class MediaPlayerCallBack : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            player!!.setDisplay(holder)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }
    }
}
