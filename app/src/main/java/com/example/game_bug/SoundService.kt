package com.example.game_bug

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast


class SoundService : Service(), MediaPlayer.OnErrorListener {
    private val mBinder: IBinder = ServiceBinder()
    private var mPlayer: MediaPlayer? = null
    private var length = 0

    internal inner class ServiceBinder : Binder() {
        fun getService(): SoundService {
            return this@SoundService
        }
    }

    override fun onBind(arg0: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mPlayer = MediaPlayer.create(this, R.raw.back)
        mPlayer!!.setOnErrorListener(this)
        if (mPlayer != null) {
            mPlayer!!.isLooping = true
            mPlayer!!.setVolume(100f, 100f)
        }
        mPlayer!!.setOnErrorListener { _, _, _ ->
            Toast.makeText(this@SoundService, "music player failed", Toast.LENGTH_SHORT).show()
            if (mPlayer != null) {
                try {
                    mPlayer!!.stop()
                    mPlayer!!.release()
                } finally {
                    mPlayer = null
                }
            }
            true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mPlayer!!.start()
        return START_STICKY
    }

    fun pauseMusic() {
        if (mPlayer!!.isPlaying) {
            mPlayer!!.pause()
            length = mPlayer!!.currentPosition
        }
    }

    fun resumeMusic() {
        if (!mPlayer!!.isPlaying) {
            mPlayer!!.seekTo(length)
            mPlayer!!.start()
        }
    }

    fun stopMusic() {
        mPlayer!!.stop()
        mPlayer!!.release()
        mPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayer != null) {
            try {
                mPlayer!!.stop()
                mPlayer!!.release()
            } finally {
                mPlayer = null
            }
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show()
        if (mPlayer != null) {
            try {
                mPlayer!!.stop()
                mPlayer!!.release()
            } finally {
                mPlayer = null
            }
        }
        return false
    }
}