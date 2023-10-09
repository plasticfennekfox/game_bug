package com.example.game_bug

import android.os.CountDownTimer


abstract class CountDownTimerPausable internal constructor(
    private var millisRemaining: Long,
    private val countDownInterval: Long
) {
    private var countDownTimer: CountDownTimer? = null
    private var isPaused = true

    private fun createCountDownTimer() {
        countDownTimer = object : CountDownTimer(millisRemaining, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                millisRemaining = millisUntilFinished
                this@CountDownTimerPausable.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                this@CountDownTimerPausable.onFinish()
            }
        }
    }

    abstract fun onTick(millisUntilFinished: Long)
    abstract fun onFinish()

    @Synchronized
    fun start(): CountDownTimerPausable {
        if (isPaused) {
            createCountDownTimer()
            countDownTimer!!.start()
            isPaused = false
        }
        return this
    }

    @Throws(IllegalStateException::class)
    fun pause() {
        if (!isPaused) {
            countDownTimer!!.cancel()
        } else {
            throw IllegalStateException("CountDownTimerPausable is already in pause state, start counter before pausing it.")
        }
        isPaused = true
    }
}