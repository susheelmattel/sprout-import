package com.sproutling.common.ui.presenter

import android.text.TextUtils
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.pojos.events.PushNotificationRegistrationEvent
import com.sproutling.common.ui.presenter.interfaces.IBasePresenter
import com.sproutling.common.ui.view.interfaces.IBaseView
import com.sproutling.common.utils.Utils
import org.greenrobot.eventbus.EventBus

/**
 * Created by subram13 on 12/14/17.
 */
open class BasePresenterImpl(baseView: IBaseView) : IBasePresenter {

    protected fun setUpPushNotification() {
        val pushToken = FirebaseInstanceId.getInstance().token
        Log.d(BaseMainPresenterImpl.TAG, "Push token: $pushToken")
        if (!TextUtils.isEmpty(pushToken)) {
            Utils.sendRegistrationToServer(BaseApplication.sInstance?.applicationContext, pushToken)
        } else {
            Log.e(BaseMainPresenterImpl.TAG, "Error registering push notification")
            Log.e(BaseMainPresenterImpl.TAG, "Push Token is empty or null")
            EventBus.getDefault().post(PushNotificationRegistrationEvent(false, null, null))
        }
    }
}