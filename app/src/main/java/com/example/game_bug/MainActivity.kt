package com.example.game_bug

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.game_bug.databinding.ActivityMainBinding


@Suppress("DEPRECATION")
class MainActivity : BaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            val binding: ActivityMainBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_main)
            binding.buttonStart.setOnClickListener {
                val editTextName = findViewById<EditText>(R.id.editTextName)
                val playerName = editTextName.text.toString()
                val editTextCount = findViewById<EditText>(R.id.editTextCount)
                val playerSettings = editTextCount.text.toString()
                val playerSettingsInt = tryParseInt(playerSettings)
                if (isNotNullOrWhiteSpace(playerName)) {
                    editTextName.error = null
                    if (playerSettingsInt in 1..100) {
                        editTextCount.error = null
                        val intent = Intent(this@MainActivity, GameActivity::class.java)
                        intent.putExtra(PLAYER_NAME_KEY, playerName)
                        intent.putExtra(PLAYER_SETTINGS_KEY, playerSettings)
                        startActivity(intent)
                    } else {
                        editTextCount.error = "Количество должно быть от 1 до 100"
                    }
                } else {
                    editTextName.error = "Имя не должно быть пустым"
                }
            }
            binding.buttonRecords.setOnClickListener {
                val intent = Intent(this@MainActivity, RecordsActivity::class.java)
                startActivity(intent)
            }
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        try {
            finish()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    public override fun onPause() {
        try {
            super.onPause()
            if (ApplicationHolder.soundService != null) ApplicationHolder.soundService!!.pauseMusic()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    public override fun onResume() {
        try {
            super.onResume()
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

    companion object {
        const val PLAYER_NAME_KEY = "com.example.user.lab2_game.PlayerName"
        const val PLAYER_SETTINGS_KEY = "com.example.user.lab2_game.PlayerSettings"
        fun tryParseInt(obj: String): Int {
            val retVal: Int = try {
                obj.toInt()
            } catch (nfe: NumberFormatException) {
                0
            }
            return retVal
        }

        fun isNotNullOrWhiteSpace(string: String?): Boolean {
            return !string.isNullOrEmpty() && string.trim { it <= ' ' }.isNotEmpty()
        }
    }
}


