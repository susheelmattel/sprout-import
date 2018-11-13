package com.sproutling.ui.fragment.presenters

import com.sproutling.ui.fragment.interfaces.ILogSleepDefaultFragment
import com.sproutling.ui.fragment.interfaces.ILogSleepDefaultPresenter
import com.sproutling.utils.LogEvents.LOG_SLEEP_MANUALLY
import com.sproutling.utils.LogEvents.START_SLEEP_TIMER
import com.sproutling.utils.Utils

/**
 * Created by subram13 on 11/1/17.
 */
class LogSleepDefaultPresenterImpl(val mLogSleepDefaultFragment: ILogSleepDefaultFragment) : ILogSleepDefaultPresenter {

    override fun handleEnterManually() {
        Utils.logEvents(LOG_SLEEP_MANUALLY)
        mLogSleepDefaultFragment.showManualLogSleep()
    }

    override fun handleStartLogSleep() {
        Utils.logEvents(START_SLEEP_TIMER)
        mLogSleepDefaultFragment.showTimerRecording()
    }
}