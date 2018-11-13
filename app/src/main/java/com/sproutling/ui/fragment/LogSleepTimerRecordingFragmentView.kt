package com.sproutling.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sproutling.R
import com.sproutling.ui.activity.interfaces.ILogSleepView
import com.sproutling.ui.fragment.interfaces.ILogSleepTimerRecordingFragment
import com.sproutling.ui.fragment.interfaces.ILogSleepTimerRecordingPresenter
import com.sproutling.ui.fragment.presenters.LogSleepTimerRecordingPresenterImpl
import com.sproutling.ui.widget.ShTextView
import com.sproutling.utils.LogEvents
import com.sproutling.utils.Utils
import kotlinx.android.synthetic.main.fragment_log_sleep_timer_recording.*

/**
 * Created by subram13 on 11/2/17.
 */
class LogSleepTimerRecordingFragmentView : BaseFragment(), ILogSleepTimerRecordingFragment {
    override fun onActionMenuClick() {
        mLogSleepTimerRecordingPresenter.saveSleep()
    }

    override fun enableSaveMenuItem(enable: Boolean) {
        (activity as LogSleepRecordingListener).enableActionMenuItem(enable)
    }

    override fun setResultAndFinish(resultEvent: Int, sleepStartTimeMs: Long) {

        (activity as LogSleepRecordingListener).setResultAndFinish(resultEvent, sleepStartTimeMs)
    }

    override fun showStartLiveTimer(show: Boolean) {
        tvStartTimer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSleepStartTimeError(show: Boolean) {
        var errorMsg: String? = null
        if (show)
            errorMsg = getString(R.string.set_sleep_dlg_start_message)
        et_fell_asleep_wrapper.error = errorMsg
    }

    override fun showSleepEndTimeError(show: Boolean, errorType: Int) {
        var errorMsg: String? = null
        if (show) {
            when (errorType) {
                ILogSleepTimerRecordingFragment.END_TIME_CANNOT_BE_FUTURE -> {
                    errorMsg = getString(R.string.set_sleep_dlg_end_message1)
                }
                ILogSleepTimerRecordingFragment.END_TIME_BEFORE_BEGIN_TIME -> {
                    errorMsg = getString(R.string.set_sleep_dlg_end_message2)
                }
                ILogSleepTimerRecordingFragment.NAP_TIME_LESS_THAN_5_MINS_OR_15_HRS -> {
                    errorMsg = getString(R.string.set_sleep_dlg_end_message3)
                }
            }

        }
        et_woke_up_wrapper.error = errorMsg
    }

    override fun showTimePicker(hour: Int?, minute: Int?, onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
        TimePickerDialog(activity, onTimeSetListener, hour!!, minute!!, false).show()
    }

    override fun goBack() {
        (activity as IBaseFragmentListener).navigateBack()
    }

    override fun showOptionsToSaveSleep() {
        val view = activity.layoutInflater.inflate(R.layout.dialog_sleeptimer, null)
        val dialog = BottomSheetDialog(activity)
        val tvSave = view.findViewById<View>(R.id.btnSave) as ShTextView
        val tvDiscard = view.findViewById<View>(R.id.btnDiscard) as ShTextView
        val tvCancel = view.findViewById<View>(R.id.btnCancel) as ShTextView
        tvSave.setOnClickListener {
            mLogSleepTimerRecordingPresenter.saveSleep()
            dialog.dismiss()
        }
        tvDiscard.setOnClickListener {
            mLogSleepTimerRecordingPresenter.discardSleep()
            Utils.logEvents(LogEvents.NAP_USER_TAPPED_DISCARD)
            dialog.dismiss()
        }
        tvCancel.setOnClickListener {
            mLogSleepTimerRecordingPresenter.cancelSleep()
            Utils.logEvents(LogEvents.NAP_USER_TAPPED_CANCEL)
            dialog.dismiss()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    override fun onBackPressed() {
        mLogSleepTimerRecordingPresenter.handleBackClick()
    }

    override fun showStartSleep(show: Boolean) {
        //Hack to don't show up the datepicker when visibility changes as this controls gets focused
        if (show) {
            et_fell_asleep.onFocusChangeListener = null
        }
        et_fell_asleep_wrapper.visibility = if (show) View.VISIBLE else View.GONE
        //Hack to don't show up the datepicker when visibility changes as this controls gets focused
//        et_fell_asleep.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) mLogSleepTimerRecordingPresenter.handleWokeUpClick() }
    }

    override fun showWakeUp(show: Boolean) {
        //Hack to don't show up the datepicker when visibility changes as this controls gets focused
        if (show) {
            et_woke_up.onFocusChangeListener = null
        }
        et_woke_up_wrapper.visibility = if (show) View.VISIBLE else View.GONE
        //Hack to don't show up the datepicker when visibility changes as this controls gets focused
//        et_woke_up.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) mLogSleepTimerRecordingPresenter.handleWokeUpClick() }
    }

    override fun setFallAsleep(sleepTime: String) {
        et_fell_asleep.setText(sleepTime, TextView.BufferType.EDITABLE)
    }

    override fun setTimer(timer: String) {
        tv_timer.text = timer
    }

    override fun setWokeUp(wokeUpTime: String) {
        et_woke_up.setText(wokeUpTime, TextView.BufferType.EDITABLE)
    }

    override fun showStopButton(show: Boolean) {
        btn_stop_timer.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "LogSleepTimerRecordingFragmentView"
    }

    private lateinit var mLogSleepTimerRecordingPresenter: ILogSleepTimerRecordingPresenter
    private var mModeType: Int = ILogSleepView.MODE_MANAUL
    private lateinit var mChildID: String

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_log_sleep_timer_recording, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mModeType = if (arguments != null && arguments.getInt(ILogSleepView.MODE_TYPE) != null) {
            arguments.getInt(ILogSleepView.MODE_TYPE)
        } else {
            ILogSleepView.MODE_MANAUL
        }
        mChildID = (if (arguments == null || arguments.getString(ILogSleepView.CHILD_ID) == null) {
        } else {
            arguments.getString(ILogSleepView.CHILD_ID)
        }).toString()
        val sleepTimeMs = arguments.getLong(ILogSleepView.SLEEP_START_TIME_MS, -1)
        mLogSleepTimerRecordingPresenter = LogSleepTimerRecordingPresenterImpl(this, mModeType, mChildID, sleepTimeMs)
        btn_stop_timer.setOnClickListener({ mLogSleepTimerRecordingPresenter.handleTimerStop() })
        init()
    }

    private fun init() {
//        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tvStartTimer.setOnClickListener { mLogSleepTimerRecordingPresenter.handleStartLiveTimerClick() }
        et_woke_up.keyListener = null
        et_woke_up.setOnClickListener({ mLogSleepTimerRecordingPresenter.handleWokeUpClick() })
        et_fell_asleep.keyListener = null
        et_fell_asleep.setOnClickListener({ mLogSleepTimerRecordingPresenter.handleFellAsleepClick() })
    }

    override fun showDatePicker(year: Int?, month: Int?, dayOfMonth: Int?, onDateSetListener: DatePickerDialog.OnDateSetListener) {
        hideKeyboard()
        DatePickerDialog(activity, onDateSetListener, year!!, month!!, dayOfMonth!!).show()
    }

    interface LogSleepRecordingListener : IBaseFragmentListener {
        fun setResultAndFinish(resultEvent: Int, sleepStartTimeMs: Long = -1)
    }
}