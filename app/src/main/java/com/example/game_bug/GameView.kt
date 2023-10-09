package com.example.game_bug

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory.*
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.game_bug.R.raw.kill
import com.example.game_bug.R.raw.miss
import java.util.UUID

@Suppress("DEPRECATION")
@SuppressLint("ViewConstructor")
class GameView internal constructor(context: Context?, playerSettings: Int?, recordItem: RecordItem?) :
    SurfaceView(context) {
    private var sounds: SoundPool? = null
    private var soundKill = 0
    private var soundMiss = 0
    private var gameLoopThread: GameLoopThread? = null
    private val sprites: MutableList<Sprite?> = ArrayList<Sprite?>()
    private val temps: MutableList<BloodSprite> = ArrayList()
    private val misses: MutableList<MissSprite> = ArrayList()
    private var recordItem: RecordItem? = null
    private var lastClick: Long = 0
    private var bmpBlood: Bitmap? = null
    private var bmpMiss: Bitmap? = null
    private var _playerSettings: Int? = null

    init {
        try {
            _playerSettings = playerSettings
            this.recordItem = recordItem
            sounds = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
            soundKill = sounds!!.load(context, kill, 1)
            soundMiss = sounds!!.load(context, miss, 1)
            gameLoopThread = GameLoopThread(this)
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    var retry = true
                    gameLoopThread!!.setRunning(false)
                    while (retry) {
                        try {
                            gameLoopThread!!.join()
                            retry = false
                        } catch (e: InterruptedException) {
                            Log.v(e.message, "error")
                        }
                    }
                }

                override fun surfaceCreated(holder: SurfaceHolder) {
                    for (i in 1.._playerSettings!!) {
                        sprites.add(createSprite())
                    }
                    gameLoopThread!!.setRunning(true)
                    gameLoopThread!!.start()
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }
            })
            bmpBlood = decodeResource(resources, R.drawable.blood1)
            bmpMiss = decodeResource(resources, R.drawable.miss)
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    private fun createSprite(): Sprite? {
        try {
            var resource = 0
            val bugType = BugType.randomBugType()
            when (bugType) {
                BugType.bk -> resource = R.drawable.bk1
                BugType.tr -> resource = R.drawable.tr1
            }
            val bmp = decodeResource(resources, resource)
            return Sprite(this, bmp, bugType)
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
        return null
    }

    public override fun onDraw(canvas: Canvas) {
        try {
            canvas.drawColor(Color.WHITE)
            @SuppressLint("DrawAllocation") val missToRemove: MutableList<MissSprite> =
                ArrayList<MissSprite>()
            @SuppressLint("DrawAllocation") val tempsToRemove: MutableList<BloodSprite> =
                ArrayList()
            @SuppressLint("DrawAllocation") val spriteIdsToRespawn: MutableList<UUID> = ArrayList()
            for (i in temps.indices.reversed()) {
                val temp = temps[i]
                temp.onDraw(canvas)
                if (temp.isRespawn) {
                    tempsToRemove.add(temp)
                    spriteIdsToRespawn.add(temp.spriteId)
                }
            }
            if (tempsToRemove.size > 0) {
                for (temp in tempsToRemove) {
                    temps.remove(temp)
                }
            }
            for (sprite in sprites) {
                if (sprite != null) {
                    if (spriteIdsToRespawn.indexOf(sprite.uniqueID) > -1) {
                        sprite.setNewRandomLocation()
                        sprite.isAlive = true
                    }
                }
                if (sprite != null) {
                    if (sprite.isAlive) {
                        sprite.onDraw(canvas)
                    }
                }
            }
            for (miss in misses) {
                miss.onDraw(canvas)
                if (miss.isRespawn) {
                    missToRemove.add(miss)
                }
            }
            if (missToRemove.size > 0) {
                for (miss in missToRemove) {
                    misses.remove(miss)
                }
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (System.currentTimeMillis() - lastClick > 300) {
            lastClick = System.currentTimeMillis()
            val x = event.x
            val y = event.y
            var isCollision = false
            synchronized(holder) {
                for (i in sprites.indices.reversed()) {
                    val sprite: Sprite? = sprites[i]
                    if (sprite != null) {
                        if (sprite.isCollision(x, y)) {
                            isCollision = true
                            sounds!!.play(soundKill, 1.0f, 1.0f, 0, 0, 1.5f)
                            when (sprite.bugType) {
                                BugType.bk -> recordItem?.RoachCount = recordItem?.RoachCount!! + 1
                                BugType.tr -> recordItem?.BeesCount = recordItem?.BeesCount!! + 1
                            }
                            sprite.isAlive = false // Устанавливаем isAlive напрямую
                            val bloodSprite = BloodSprite(
                                sprite.uniqueID, this, x, y,
                                bmpBlood!!
                            )
                            temps.add(bloodSprite)
                            break
                        }
                    }
                }
            }
            if (isCollision) recordItem?.KillCount = recordItem?.KillCount!! + 1 else {
                sounds!!.play(soundMiss, 1.0f, 1.0f, 0, 0, 1.5f)
                val missSprite = bmpMiss?.let { MissSprite(this, x, y, it) }
                if (missSprite != null) {
                    misses.add(missSprite)
                }
                recordItem?.KillCount = recordItem?.KillCount!! - 1
            }
        }
        return true
    }

    fun resume() {
        try {
            gameLoopThread = GameLoopThread(this)
            gameLoopThread!!.setRunning(true)
            gameLoopThread!!.start()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    fun pause() {
        try {
            gameLoopThread!!.setRunning(false)
            var retry = true
            while (retry) {
                try {
                    gameLoopThread!!.join()
                    retry = false
                } catch (e: InterruptedException) {
                    Log.v(e.message, "error")
                }
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }
}