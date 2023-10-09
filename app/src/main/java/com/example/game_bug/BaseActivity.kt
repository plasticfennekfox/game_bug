package com.example.game_bug

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


abstract class BaseActivity : AppCompatActivity() {private val serviceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        try {
            ApplicationHolder.soundService = (binder as SoundService.ServiceBinder).getService()
            ApplicationHolder.soundService?.resumeMusic()
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        try {
            ApplicationHolder.soundService = null
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // В этом месте можно вызвать BindSoundService(),
        // если активности требуется связь со службой.
        BindSoundService()
    }

    override fun onDestroy() {
        // В этом месте можно вызвать UnbindSoundService() перед уничтожением активности.
        UnbindSoundService()
        super.onDestroy()
    }

    protected fun BindSoundService() {
        try {
            bindService(Intent(this, SoundService::class.java), serviceConnection, BIND_AUTO_CREATE)
            ApplicationHolder.soundServiceIsBound = true
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }

    protected fun UnbindSoundService() {
        try {
            if (ApplicationHolder.soundServiceIsBound) {
                unbindService(serviceConnection)
                ApplicationHolder.soundServiceIsBound = false
            }
        } catch (ex: Exception) {
            Log.v(ex.message, "error")
        }
    }
}
