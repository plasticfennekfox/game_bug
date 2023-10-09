package com.example.game_bug

import java.util.Arrays

import java.util.Collections
import java.util.Random


enum class BugType {
    bk, tr;

    companion object {
        private val VALUES = Collections.unmodifiableList(listOf(*values()))
        private val SIZE = VALUES.size
        private val RANDOM: Random = Random()
        fun randomBugType(): BugType {
            return VALUES[RANDOM.nextInt(SIZE)]
        }
    }
}
