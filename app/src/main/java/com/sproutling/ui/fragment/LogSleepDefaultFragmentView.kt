package com.sproutling.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sproutling.R
import com.sproutling.ui.fragment.interfaces.ILogSleepDefaultFragment
import com.sproutling.ui.fragment.interfaces.ILogSleepDefaultPresenter
import com.sproutling.ui.fragment.presenters.LogSleepDefaultPresenterImpl
import kotlinx.android.synthetic.main.fragment_log_sleep_default_layout.*

/**
 * Created by subram13 on 11/1/17.
 */
class LogSleepDefaultFragmentView : BaseFragment(), ILogSleepDefaultFragment {
    override fun onBackPressed() {
        mLogSleepDefaultListener.navigateBack()
    }

    private lateinit var mLogSleepDefaultPresenter: ILogSleepDefaultPresenter
    private lateinit var mLogSleepDefaultListener: ILogSleepDefaultListener


    companion object {
        const val TAG = "LogSleepDefaultFragmentView"
    }

    fun newInstance(): LogSleepDefaultFragmentView {
        return LogSleepDefaultFragmentView()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_log_sleep_default_layout, container, false)
        return view!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLogSleepDefaultPresenter = LogSleepDefaultPresenterImpl(this)
        mLogSleepDefaultListener.enableActionMenuItem(false)
        tvEnterManually.setOnClickListener({ (mLogSleepDefaultPresenter as LogSleepDefaultPresenterImpl).handleEnterManually() })
        btnStartLogSleep.setOnClickListener({ (mLogSleepDefaultPresenter as LogSleepDefaultPresenterImpl).handleStartLogSleep() })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        initListener(context as Activity)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        initListener(activity)
    }

    private fun initListener(activity: Activity?) {
        mLogSleepDefaultListener = activity as ILogSleepDefaultListener
    }

    override fun showTimerRecording() {
        mLogSleepDefaultListener.onLogSleepStartClick()
    }

    override fun showManualLogSleep() {
        mLogSleepDefaultListener.onEnterManuallyClick()
    }

    interface ILogSleepDefaultListener : IBaseFragmentListener {
        fun onLogSleepStartClick()
        fun onEnterManuallyClick()

    }
}