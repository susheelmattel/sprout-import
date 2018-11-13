package com.sproutling.ui.activity.interfaces

/**
 * Created by subram13 on 11/1/17.
 */
interface ILogSleepView {
    fun loadDefaultView()

    fun showTimerView(sleepStartTimeMs:Long)

    fun saveLoggedSleep()

    companion object {
        const val MODE_MANAUL = 1
        const val MODE_TIMER = 2
        const val MODE_TYPE = "MODE_TYPE"
        const val CHILD_ID = "CHILD_ID"
        const val SLEEP_START_TIME_MS = "SLEEP_START_TIME_MS"
    }

}