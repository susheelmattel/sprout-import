package com.sproutling.common.app

import android.content.Context
import com.fisherprice.smartconnect.FPSConnectApplication
import java.util.*

/**
 * Created by subram13 on 12/19/17.
 */

abstract class BaseApplication : FPSConnectApplication() {

    override fun onCreate() {
        super.onCreate()
        sInstance = this
    }

    companion object {
        var sInstance: BaseApplication? = null
        const val TAG = "BaseApplication"
    }

    fun getAppContext(): Context {
        return applicationContext
    }

    fun getAppPackage(): String {
        return packageName
    }

    abstract fun initializeMqtt()

    fun canQueryForUpdate(): Boolean {
        return if (mNextUpdateQueryTime == null) {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.HOUR_OF_DAY, TIME_FOR_NEXT_QUERY_HOURS)
            mNextUpdateQueryTime = cal.time
            true
        } else {
            var currentTime = Calendar.getInstance().time
            var retVal = currentTime.after(mNextUpdateQueryTime)
            retVal
        }
    }

    var mNextUpdateQueryTime: Date? = null

    private val TIME_FOR_NEXT_QUERY_HOURS = 24
}
