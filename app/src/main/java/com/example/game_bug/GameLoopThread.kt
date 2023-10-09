package com.example.game_bug

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder


class GameLoopThread internal constructor(view: GameView) : Thread() {
    private val view: GameView
    private var running = false

    init {
        this.view = view
    }

    fun setRunning(run: Boolean) {
        running = run
    }

    @SuppressLint("WrongCall")
    override fun run() {
        val ticksPS = 1000 / FPS
        var startTime: Long
        var sleepTime: Long
        while (running) {
            var c: Canvas? = null
            startTime = System.currentTimeMillis()
            val holder: SurfaceHolder = view.holder
            try {
                c = holder.lockCanvas()
                synchronized(view.holder) { view.onDraw(c) }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c)
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime)
            try {
                if (sleepTime > 0) sleep(sleepTime) else sleep(10)
            } catch (e: Exception) {
                Log.v(e.message, "error")
            }
        }
    }

    companion object {
        private const val FPS: Long = 15
    }
}