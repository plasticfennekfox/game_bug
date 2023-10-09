package com.example.game_bug

import android.app.Application


object ApplicationHolder : Application() {
    var soundService: SoundService? = null
    var soundServiceIsBound = false
}
