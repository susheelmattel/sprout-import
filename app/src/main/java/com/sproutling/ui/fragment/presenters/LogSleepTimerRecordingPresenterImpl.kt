package com.sproutling.ui.fragment.presenters

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Handler
import android.os.Message
import android.util.Log
import com.sproutling.App
import com.sproutling.api.SproutlingApi.createEvent
import com.sproutling.apiservices.EventApiBuilder
import com.sproutling.pojos.CreateEventRequestBody
import com.sproutling.services.AccountManagement
import com.sproutling.ui.activity.LogSleepView
import com.sproutling.ui.activity.interfaces.ILogSleepView
import com.sproutling.ui.fragment.interfaces.ILogSleepTimerRecordingFragment
import com.sproutling.ui.fragment.interfaces.ILogSleepTimerRecordingPresenter
import com.sproutling.utils.DateTimeUtils
import com.sproutling.utils.DateTimeUtils.FIFTEEN_HOURS_IN_MS
import com.sproutling.utils.DateTimeUtils.FIVE_MINUTE_IN_MS
import com.sproutling.utils.LogEvents
import com.sproutling.utils.SharedPrefManager
import com.sproutling.utils.Utils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

/**
 * Created by subram13 on 11/2/17.
 */
class LogSleepTimerRecordingPresenterImpl(logSleepTimerRecordingFragment: ILogSleepTimerRecordingFragment, modeType: Int, childID: String, sleepTimeMs: Long) : ILogSleepTimerRecordingPresenter {
    override fun handleStartLiveTimerClick() {
        mModeType = ILogSleepView.MODE_TIMER
        mSleepEndTime = null
        initTimerMode()
    }

    override fun handleFellAsleepClick() {
        mLogSleepTimerRecordingFragment.showDatePicker(mSleepStartTime?.get(Calendar.YEAR), mSleepStartTime?.get(Calendar.MONTH), mSleepStartTime?.get(Calendar.DAY_OF_MONTH),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    mLogSleepTimerRecordingFragment.showTimePicker(mSleepStartTime?.get(Calendar.HOUR_OF_DAY), mSleepStartTime?.get(Calendar.MINUTE),
                            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                mSleepStartTime?.set(Calendar.YEAR, year)
                                mSleepStartTime?.set(Calendar.MONTH, month)
                                mSleepStartTime?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                mSleepStartTime?.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                mSleepStartTime?.set(Calendar.MINUTE, minute)
                                mLogSleepTimerRecordingFragment.showSleepStartTimeError(false)
                                mLogSleepTimerRecordingFragment.setFallAsleep(DateTimeUtils.getFormattedDateTime1(mSleepStartTime?.time))
                                updateTimerValue(DateTimeUtils.getCurrentTimeDifference(mSleepStartTime, mSleepEndTime))
                                validateSleepTimes()
                            })
                })
    }

    override fun handleWokeUpClick() {
        if (mSleepEndTime == null) {
            mSleepEndTime = Calendar.getInstance()
        }
        mLogSleepTimerRecordingFragment.showDatePicker(mSleepEndTime?.get(Calendar.YEAR), mSleepEndTime?.get(Calendar.MONTH), mSleepEndTime?.get(Calendar.DAY_OF_MONTH),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    mLogSleepTimerRecordingFragment.showTimePicker(mSleepEndTime?.get(Calendar.HOUR_OF_DAY), mSleepEndTime?.get(Calendar.MINUTE),
                            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                                mSleepEndTime?.set(Calendar.YEAR, year)
                                mSleepEndTime?.set(Calendar.MONTH, month)
                                mSleepEndTime?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                mSleepEndTime?.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                mSleepEndTime?.set(Calendar.MINUTE, minute)
                                mLogSleepTimerRecordingFragment.setWokeUp(DateTimeUtils.getFormattedDateTime1(mSleepEndTime?.time))
                                updateTimerValue(DateTimeUtils.getCurrentTimeDifference(mSleepStartTime, mSleepEndTime))
                                validateSleepTimes()
                            })
                })
    }

    private fun validateSleepEndTimeAndGetErrorType(): Int {
        var retVal = -1
        if (mSleepEndTime != null && mSleepStartTime != null) {
            when {
                mSleepEndTime?.timeInMillis!! < mSleepStartTime!!.timeInMillis -> retVal = ILogSleepTimerRecordingFragment.END_TIME_BEFORE_BEGIN_TIME
                mSleepEndTime?.timeInMillis!! > Calendar.getInstance(Locale.getDefault()).timeInMillis -> retVal = ILogSleepTimerRecordingFragment.END_TIME_CANNOT_BE_FUTURE
                ((mSleepEndTime?.timeInMillis!! - mSleepStartTime?.timeInMillis!!) < FIVE_MINUTE_IN_MS) ||
                        ((mSleepEndTime?.timeInMillis!! - mSleepStartTime?.timeInMillis!!) > FIFTEEN_HOURS_IN_MS) -> retVal = ILogSleepTimerRecordingFragment.NAP_TIME_LESS_THAN_5_MINS_OR_15_HRS
            }
        }
        return retVal
    }

    private fun validateSleepTimes(): Boolean {
        var hasError = false
        if (mSleepStartTime?.timeInMillis!! > Calendar.getInstance().timeInMillis) {
            mLogSleepTimerRecordingFragment.showSleepStartTimeError(true)
            hasError = true
        } else {
            mLogSleepTimerRecordingFragment.showSleepStartTimeError(false)
        }
        val errorType = validateSleepEndTimeAndGetErrorType()
        if (errorType == -1) {
            mLogSleepTimerRecordingFragment.showSleepEndTimeError(false, errorType)
        } else {
            mLogSleepTimerRecordingFragment.showSleepEndTimeError(true, errorType)
            hasError = true
        }
        //enable Save button if no errors
        mLogSleepTimerRecordingFragment.enableSaveMenuItem(!hasError)

        return hasError
    }

    override fun saveSleep() {
        if (!validateSleepTimes()) {
            mLogSleepTimerRecordingFragment.showProgressBar(true)
            createEvent(CreateEventRequestBody(mChildId!!, mSleepEndTime!!.time, mSleepStartTime!!.time, EventApiBuilder.EventApi.EVENT_NAP),
                    object : retrofit2.Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                            mLogSleepTimerRecordingFragment.showProgressBar(false)
                        }

                        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                            mLogSleepTimerRecordingFragment.showProgressBar(false)
                            if (response!!.isSuccessful) {
                                Log.d(TAG, "Sleep saved successfully")
                                Utils.logEvents(LogEvents.NAP_SAVED)
                                SharedPrefManager.put(App.getAppContext(), SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (-1).toLong())
                                mLogSleepTimerRecordingFragment.setResultAndFinish(LogSleepView.Status.STOP)
                            }
                        }
                    }, AccountManagement.getInstance(App.getInstance()).user.accessToken)
        }

    }

    override fun discardSleep() {
        Utils.logEvents(LogEvents.NAP_USER_TAPPED_DISCARD)
        SharedPrefManager.put(App.getAppContext(), SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, false)
        SharedPrefManager.put(App.getAppContext(), SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, (-1).toLong())
        mLogSleepTimerRecordingFragment.goBack()
    }

    override fun cancelSleep() {
        Utils.logEvents(LogEvents.NAP_USER_TAPPED_CANCEL)
    }

    override fun handleBackClick() {
        if (mSleepEndTime != null && mSleepStartTime != null) {
            mLogSleepTimerRecordingFragment.showOptionsToSaveSleep()
        } else {
            mSleepTimer.cancel()
            if (mCurrentStatus == LogSleepView.Status.RECORDING) {
                mLogSleepTimerRecordingFragment.setResultAndFinish(LogSleepView.Status.RECORDING, mSleepStartTime!!.timeInMillis)
            } else
                mLogSleepTimerRecordingFragment.goBack()
        }
    }

    companion object {
        const val TAG = "LgSlpTmrRecdgPrsntrImpl"
    }

    private var mSleepStartTime: Calendar? = null
    private var mSleepEndTime: Calendar? = null
    private var mLogSleepTimerRecordingFragment = logSleepTimerRecordingFragment
    private var mModeType = modeType
    private var mChildId: String? = childID
    private var mCurrentStatus: Int = LogSleepView.Status.START

    private var mSleepTimer = Timer("Schedule", true)

    init {
        mLogSleepTimerRecordingFragment.showStartSleep(true)
        mLogSleepTimerRecordingFragment.enableSaveMenuItem(false)
        if (sleepTimeMs == -1L) {
            mSleepStartTime = Calendar.getInstance(Locale.getDefault())
            val timeMs = mSleepStartTime!!.timeInMillis
            SharedPrefManager.put(App.getAppContext(), SharedPrefManager.SPKey.LONG_SLEEP_TIMER_START_TIME, timeMs)
        } else {
            val calender = Calendar.getInstance(Locale.getDefault())
            calender.timeInMillis = sleepTimeMs
            mSleepStartTime = calender
        }

        mLogSleepTimerRecordingFragment.setFallAsleep(DateTimeUtils.getFormattedDateTime1(mSleepStartTime?.time))
        if (mModeType == ILogSleepView.MODE_TIMER) {
            initTimerMode()
            if (sleepTimeMs == -1L) {
                Utils.logEvents(LogEvents.NAP_STARTED)
                SharedPrefManager.put(App.getAppContext(), SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, true)
            }
        } else {
            initManualMode()
        }
    }

    private var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val timeString = msg.obj as String
            mLogSleepTimerRecordingFragment.setTimer(timeString)
        }
    }

    private fun initManualMode() {
        mLogSleepTimerRecordingFragment.showWakeUp(true)
        mLogSleepTimerRecordingFragment.showStopButton(false)
        mLogSleepTimerRecordingFragment.showStartLiveTimer(true)
        updateTimerValue(DateTimeUtils.getCurrentTimeDifference(mSleepStartTime, mSleepEndTime))
    }

    private fun initTimerMode() {
        mLogSleepTimerRecordingFragment.showWakeUp(false)
        mLogSleepTimerRecordingFragment.showStopButton(true)
        mLogSleepTimerRecordingFragment.showStartLiveTimer(false)
        startSleepTimer()
    }

    private fun startSleepTimer() {
        mCurrentStatus = LogSleepView.Status.RECORDING
        mSleepEndTime = null
        updateTimerValue(DateTimeUtils.getCurrentTimeDifference(mSleepStartTime, mSleepEndTime))
        mSleepTimer.scheduleAtFixedRate(0, 1000) {
            var time = DateTimeUtils.getCurrentTimeDifference(mSleepStartTime, mSleepEndTime)
            Log.d(TAG, "Time:" + time)
            val message = Message()
            message.obj = time
            mHandler?.sendMessage(message)
        }
    }

    private fun updateTimerValue(timerValue: String) {
        mLogSleepTimerRecordingFragment.setTimer(timerValue)
    }

    override fun handleTimerStop() {
        Utils.logEvents(LogEvents.NAP_ENDED)
        SharedPrefManager.put(App.getAppContext(), SharedPrefManager.SPKey.BOOL_SLEEP_TIMER_RECORDING, false)
        mSleepEndTime = Calendar.getInstance(Locale.getDefault())
        Log.d(TAG, "SleepEndTime" + mSleepEndTime?.time.toString())
        updateTimerValue(DateTimeUtils.getCurrentTimeDifference(mSleepStartTime, mSleepEndTime))
        mSleepTimer.cancel()
        mLogSleepTimerRecordingFragment.setWokeUp(DateTimeUtils.getFormattedDateTime1(mSleepEndTime?.time))
        mLogSleepTimerRecordingFragment.showWakeUp(true)
        mLogSleepTimerRecordingFragment.showStopButton(false)
        validateSleepTimes()
    }


}