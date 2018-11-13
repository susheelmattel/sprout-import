package com.sproutling.ui.fragment.interfaces

/**
 * Created by subram13 on 11/2/17.
 */
interface ILogSleepTimerRecordingPresenter {
    fun handleTimerStop()
    fun handleBackClick()
    fun saveSleep()
    fun discardSleep()
    fun cancelSleep()
    fun handleFellAsleepClick()
    fun handleWokeUpClick()
    fun handleStartLiveTimerClick()
}