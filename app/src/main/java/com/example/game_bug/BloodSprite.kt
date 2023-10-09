package com.example.game_bug

import android.graphics.Bitmap
import android.graphics.Canvas
import java.util.UUID


internal class BloodSprite(
    var spriteId: UUID,
    gameView: GameView,
    x: Float,
    y: Float,
    private val bmp: Bitmap
) {
    private val x: Float
    private val y: Float
    private var timeCounter = 0

    init {
        this.x = Math.min((x - (bmp.width / 2f)).coerceAtLeast(0f), (gameView.width - bmp.width).toFloat())
        this.y = Math.min((y - (bmp.height / 2f)).coerceAtLeast(0f), (gameView.height - bmp.height).toFloat())
    }

    fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bmp, x, y, null)
    }

    val isRespawn: Boolean
        get() {
            val TIME_LIMIT = 15
            if (timeCounter >= TIME_LIMIT) {
                return true
            }
            timeCounter++
            return false
        }
}