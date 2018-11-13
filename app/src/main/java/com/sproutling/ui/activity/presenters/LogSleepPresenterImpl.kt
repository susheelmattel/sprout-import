package com.sproutling.ui.activity.presenters

import com.sproutling.App
import com.sproutling.ui.activity.interfaces.ILogSleepPresenter
import com.sproutling.ui.activity.interfaces.ILogSleepView
import com.sproutling.utils.SharedPrefManager

/**
 * Created by subram13 on 11/1/17.
 */
class LogSleepPresenterImpl(val logSleepView: ILogSleepView) : ILogSleepPresenter {
    override fun handleActionMenuClick() {
        mLogSleepView.saveLoggedSleep()
    }

    private var mLogSleepView = logSleepView

    init {
        mLogSleepView.loadDefaultView()
        val startTimeRecord = SharedPrefManager.getLong(App.getAppContext(), SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (-1).toLong())
        if (startTimeRecord != -1L) {
            mLogSleepView.showTimerView(startTimeRecord)
        }
    }


}