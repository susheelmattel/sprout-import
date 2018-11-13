package com.sproutling.ui.fragment.interfaces

import android.app.DatePickerDialog
import android.app.TimePickerDialog

/**
 * Created by subram13 on 11/2/17.
 */
interface ILogSleepTimerRecordingFragment : IBaseFragment {
    fun setFallAsleep(sleepTime: String)
    fun setTimer(timer: String)
    fun setWokeUp(wokeUpTime: String)
    fun showStopButton(show: Boolean)
    fun showWakeUp(show: Boolean)
    fun showStartSleep(show: Boolean)
    fun showOptionsToSaveSleep()
    fun goBack()
    fun showDatePicker(year: Int?, month: Int?, dayOfMonth: Int?, onDateSetListener: DatePickerDialog.OnDateSetListener)
    fun showTimePicker(hour: Int?, minute: Int?, onTimeSetListener: TimePickerDialog.OnTimeSetListener)
    fun showSleepStartTimeError(show: Boolean)
    fun showSleepEndTimeError(show: Boolean, errorType: Int)
    fun showStartLiveTimer(show: Boolean)
    fun enableSaveMenuItem(enable:Boolean)
    fun setResultAndFinish(resultEvent: Int, sleepStartTimeMs: Long = -1)
    fun onActionMenuClick()

    companion object {
        const val END_TIME_BEFORE_BEGIN_TIME = 2
        const val END_TIME_CANNOT_BE_FUTURE = 1
        const val NAP_TIME_LESS_THAN_5_MINS_OR_15_HRS = 3
    }

}