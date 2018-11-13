package com.sproutling.common.ui.presenter

import android.util.Log
import com.sproutling.api.SproutlingApi
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ui.presenter.interfaces.IBaseSignInPresenter
import com.sproutling.common.ui.view.interfaces.IBaseSignInView
import com.sproutling.common.utils.AccountManagement
import com.sproutling.common.utils.Utils
import com.sproutling.events.ServiceNetworkEvent
import com.sproutling.interceptor.NoConnectivityException
import com.sproutling.pojos.LoginRequestBody
import com.sproutling.pojos.LoginResponse
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by subram13 on 12/14/17.
 */
abstract class BaseSignInPresenterImpl(baseSignInView: IBaseSignInView) : BasePresenterImpl(baseSignInView), IBaseSignInPresenter {

    override fun handleUserIDFocusChange(hasFocus: Boolean, userID: String) {
        if (!hasFocus) {
            mSignInView.showUserIDError(!Utils.isUserIdValid(userID))
        }
    }

    override fun validateUserID(userID: String) {
        mIsUserIDValid = Utils.isUserIdValid(userID)
        mSignInView.enableSignInButton(mIsPasswordValid && mIsUserIDValid)
    }


    override fun validatePassword(password: String) {
        mIsPasswordValid = password.isNotEmpty() &&
                password.length >= PASSWORD_MIN &&
                password.length <= PASSWORD_MAX
        mSignInView.enableSignInButton(mIsPasswordValid && mIsUserIDValid)
    }

    override fun onSignInClicked(userID: String, password: String) {
        if (mIsUserIDValid && mIsPasswordValid) {
            var formattedUserID = if (Utils.isPhoneValid(userID)) Utils.prefixCountryCode(userID) else userID
            mSignInView.showProgressBar(true)
            SproutlingApi.login(LoginRequestBody(formattedUserID, password), object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                    if (response!!.isSuccessful) {
                        Log.d(TAG, "SignIn Success")
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).saveUserAccount(response.body())
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).writePassword(password)
                        BaseApplication.sInstance!!.initializeMqtt()
                        setUpPushNotification()
                    } else {
                        mSignInView.showProgressBar(false)
                        mSignInView.showInvalidLoginDialog()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                    mSignInView.showProgressBar(false)
                    if(t!!.message!!.contentEquals(NoConnectivityException.CONNECTION_EXCEPTION)) {
                        EventBus.getDefault().post(ServiceNetworkEvent(true))
                    }
                }
            })
        }
    }

    override fun onForgotPasswordClicked() {
        mSignInView.showForgotPasswordUI()
    }

    override fun onUpdateServerUrlClicked() {
        mSignInView.showUpdateServerUrlDialog()
    }

    companion object {
        const val PASSWORD_MIN = 8
        const val PASSWORD_MAX = 72
        const val TAG = "BaseSignInPresenterImpl"
    }

    private var mSignInView = baseSignInView
    private var mIsUserIDValid = false
    private var mIsPasswordValid = false
}