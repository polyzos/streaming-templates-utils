package io.ipolyzos.utils


object TimeUtils {
    private const val second: Long = 1000000000
    private var lastTime: Long = 0

    private fun getDeltaTime(): Long {
        return System.nanoTime() - lastTime
    }

    fun updateTime() {
        lastTime = System.nanoTime()
    }

    fun hasSecondPassed(): Boolean {
        return if (getDeltaTime() >= second) {
            updateTime()
            true
        } else false
    }
}