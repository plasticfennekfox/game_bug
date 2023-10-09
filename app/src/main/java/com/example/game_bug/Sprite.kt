package com.example.game_bug

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import java.util.Random
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.roundToInt

internal class Sprite(
    private val gameView: GameView,
    private val bmp: Bitmap,
    var bugType: BugType
) {
    // direction = 0 up, 1 left, 2 down, 3 right,
    // animation = 3 back, 1 left, 0 front, 2 right
    private val DIRECTION_TO_ANIMATION_MAP = intArrayOf(3, 1, 0, 2)
    private var x = 0
    private var y = 0
    private var xSpeed = 0
    private var ySpeed = 0
    private var currentFrame = 0
    private val width: Int = bmp.width / BMP_COLUMNS
    private val height: Int = bmp.height / BMP_ROWS
    var isAlive = true
    val uniqueID: UUID = UUID.randomUUID()

    init {
        setNewRandomLocation()
    }

    fun setNewRandomLocation() {
        val rnd = Random()
        x = rnd.nextInt(gameView.width - width)
        y = rnd.nextInt(gameView.height - height)
        xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED
        ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED
    }

    private fun update() {
        if (x >= gameView.width - width - xSpeed || x + xSpeed <= 0) {
            xSpeed = -xSpeed
        }
        x += xSpeed
        if (y >= gameView.height - height - ySpeed || y + ySpeed <= 0) {
            ySpeed = -ySpeed
        }
        y += ySpeed
        currentFrame = ++currentFrame % BMP_COLUMNS
    }

    fun onDraw(canvas: Canvas) {
        update()
        val srcX = currentFrame * width
        val srcY = animationRow * height
        val src = Rect(srcX, srcY, srcX + width, srcY + height)
        val dst = Rect(x, y, x + width, y + height)
        canvas.drawBitmap(bmp, src, dst, null)
    }

    private val animationRow: Int
        get() {
            val dirDouble = atan2(xSpeed.toDouble(), ySpeed.toDouble()) / (Math.PI / 2) + 2
            val direction = dirDouble.roundToInt() % BMP_ROWS
            return DIRECTION_TO_ANIMATION_MAP[direction]
        }

    fun isCollision(x2: Float, y2: Float): Boolean {
        return x2 > x && x2 < x + width && y2 > y && y2 < y + height
    }

    companion object {
        private const val BMP_ROWS = 4
        private const val BMP_COLUMNS = 3
        private const val MAX_SPEED = 10
    }
}