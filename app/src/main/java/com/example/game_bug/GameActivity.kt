package com.example.game_bug

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import java.text.DateFormat
import java.util.Date


@Suppress("DEPRECATION")
open class GameActivity : BaseActivity() {
    private var gameView: GameView? = null
    protected var secondsRemainingText: TextView? = null
    protected var killCountText: TextView? = null
    private val recordItem: RecordItem = RecordItem()
    private var timer: CountDownTimerPausable? = null
    private var processStarted: Boolean? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            processStarted = false
            val i = intent
            val playerName = i.getStringExtra(MainActivity.PLAYER_NAME_KEY)
            val playerSettings = i.getStringExtra(MainActivity.PLAYER_SETTINGS_KEY)!!.toInt()
            recordItem.Name = playerName
            gameView = GameView(this, playerSettings, recordItem)
            setContentView(gameView)
            secondsRemainingText = TextView(this)
            addContentView(
                secondsRemainingText,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            killCountText = TextView(this)
            killCountText!!.setPadding(0, 40, 0, 0)
            addContentView(
                killCountText,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            val TIME_LIMIT = 120
            timer = object : CountDownTimerPausable((TIME_LIMIT * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val s = millisUntilFinished / 1000
                    val remainingText = "Осталось времени: $s с"
                    secondsRemainingText!!.text = remainingText
                    if (s <= 15) {
                        secondsRemainingText!!.setTextColor(Color.RED)
                    }
                    val killText = "Убито насекомых: " + recordItem.KillCount
                    killCountText!!.text = killText
                    processStarted = true
                }

                override fun onFinish() {
                    val intent = Intent(this@GameActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@GameActivity, "Игра окончена", Toast.LENGTH_LONG).show()
                }
            }.start()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            return when (item.itemId) {
                R.id.activity_main -> {
                    onBackPressed()
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onStop() {
        try {
            super.onStop()
            try {
                val st = StoredData()
                recordItem.CreatedAt =
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(Date())
                st.saveNewRecord(this, recordItem)
            } catch (ex: Exception) {
                Log.v(ex.message, "error")
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        try {
            gameView?.pause()
            timer!!.pause()
            AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите выйти в меню?")
                .setCancelable(false)
                .setPositiveButton("Да") { _, _ ->
                    val intent = Intent(this@GameActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Нет") { _, _ ->
                    gameView?.resume()
                    timer!!.start()
                }
                .show()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    override fun onPause() {
        try {
            super.onPause()
            if (processStarted!!) {
                gameView?.pause()
            }
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.pauseMusic()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    override fun onResume() {
        try {
            super.onResume()
            if (processStarted!!) {
                gameView?.resume()
            }
            if (ApplicationHolder.soundService == null) BindSoundService()
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.resumeMusic()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    public override fun onDestroy() {
        try {
            super.onDestroy()
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.stopMusic()
            if (ApplicationHolder.soundService != null) UnbindSoundService()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }
}
