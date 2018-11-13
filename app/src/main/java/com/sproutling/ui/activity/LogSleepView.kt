package com.sproutling.ui.activity

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import com.sproutling.R
import com.sproutling.ui.activity.interfaces.ILogSleepPresenter
import com.sproutling.ui.activity.interfaces.ILogSleepView
import com.sproutling.ui.activity.interfaces.ILogSleepView.Companion.CHILD_ID
import com.sproutling.ui.activity.presenters.LogSleepPresenterImpl
import com.sproutling.ui.fragment.LogSleepDefaultFragmentView
import com.sproutling.ui.fragment.LogSleepTimerRecordingFragmentView
import com.sproutling.ui.fragment.interfaces.IBaseFragment
import com.sproutling.ui.fragment.interfaces.ILogSleepTimerRecordingFragment


/**
 * Created by subram13 on 11/1/17.
 */
class LogSleepView : BaseActivity(), ILogSleepView, LogSleepDefaultFragmentView.ILogSleepDefaultListener, LogSleepTimerRecordingFragmentView.LogSleepRecordingListener {
    override fun saveLoggedSleep() {
        mCurrentFragment = fragmentManager.findFragmentById(R.id.fragment)
        if (mCurrentFragment is ILogSleepTimerRecordingFragment) {
            (mCurrentFragment as ILogSleepTimerRecordingFragment).onActionMenuClick()
        }
    }

    override fun enableActionMenuItem(enable: Boolean) {
        setActionButtonEnable(enable)
    }

    override fun showTimerView(sleepStartTimeMs: Long) {
        showLogSleepTimer(sleepStartTimeMs)
    }

    override fun setResultAndFinish(resultEvent: Int, sleepStartTimeMs: Long) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putLong(ILogSleepView.SLEEP_START_TIME_MS, sleepStartTimeMs)
        intent.putExtras(bundle)
        setResult(resultEvent, intent)
        finish()
    }

    override fun navigateBack() {
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            setResultAndFinish(LogSleepView.Status.STOP)
        }

    }


    private lateinit var mLogSleepPresenter: ILogSleepPresenter
    private var mCurrentFragment: Fragment? = null
    private lateinit var mChildID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_sleep)
        if (intent.extras != null) {
            mChildID = intent.extras.getString(CHILD_ID, null)
        }
        mLogSleepPresenter = LogSleepPresenterImpl(this)

    }

    private fun replaceFragmentView(fragment: Fragment, tag: String) {
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment, tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }


    override fun onStart() {
        super.onStart()
        setUpToolbar()
        initActionBar()
        setToolBarTitle(getString(R.string.log_sleep))
        setActionMenuTitle(getString(R.string.save))
        setActionMenuClickListener({
            mLogSleepPresenter.handleActionMenuClick()
        })
        setActionButtonEnable(false)
        setBackDrawable(R.drawable.ic_android_back_white)
    }

    companion object {
        const val TAG = "LogSleepView"
    }

    object Status {
        const val START = 0
        const val RECORDING = 1
        const val STOP = 2
        const val MANUALLY = 3

    }

    override fun onBackPressed() {
        mCurrentFragment = fragmentManager.findFragmentById(R.id.fragment)
        (mCurrentFragment as IBaseFragment).onBackPressed()
    }

    override fun loadDefaultView() {
        replaceFragmentView(LogSleepDefaultFragmentView(), LogSleepDefaultFragmentView.TAG)
    }

    override fun onLogSleepStartClick() {
        showLogSleepTimer()
    }

    private fun showLogSleepTimer(sleepStartTimeMs: Long = -1) {
        var logSleepTimerRecordingFragmentView = LogSleepTimerRecordingFragmentView()
        logSleepTimerRecordingFragmentView.arguments = getBundle(ILogSleepView.MODE_TIMER, sleepStartTimeMs)
        replaceFragmentView(logSleepTimerRecordingFragmentView, LogSleepTimerRecordingFragmentView.TAG)
    }

    override fun onEnterManuallyClick() {
        var logSleepTimerRecordingFragmentView = LogSleepTimerRecordingFragmentView()
        logSleepTimerRecordingFragmentView.arguments = getBundle(ILogSleepView.MODE_MANAUL)
        replaceFragmentView(logSleepTimerRecordingFragmentView, LogSleepTimerRecordingFragmentView.TAG)
    }

    private fun getBundle(modeType: Int, sleepStartTimeMs: Long = -1): Bundle {
        var bundle = Bundle()
        bundle.putInt(ILogSleepView.MODE_TYPE, modeType)
        bundle.putLong(ILogSleepView.SLEEP_START_TIME_MS, sleepStartTimeMs)
        bundle.putString(ILogSleepView.CHILD_ID, mChildID)
        return bundle
    }
}