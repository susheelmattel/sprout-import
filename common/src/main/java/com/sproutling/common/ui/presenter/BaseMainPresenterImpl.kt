package com.sproutling.common.ui.presenter

import android.os.Handler
import android.util.Log
import com.sproutling.api.SproutlingApi
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.managers.OTAManager
import com.sproutling.common.ui.presenter.interfaces.IBaseMainPresenter
import com.sproutling.common.ui.view.interfaces.IBaseMainView
import com.sproutling.common.utils.AccountManagement
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.Child
import com.sproutling.pojos.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/**
 * Created by subram13 on 12/14/17.
 */
abstract class BaseMainPresenterImpl(baseMainView: IBaseMainView) : BasePresenterImpl(baseMainView), IBaseMainPresenter {
    override fun handleOnUpdateClick(url: String) {
        mBaseMainView.openUpdateUrl(url)
    }

    companion object {
        const val TAG = "BaseMainPresenterImpl"
        const val SPLASH_DURATION = 2000L
    }

    private var mBaseMainView = baseMainView
    protected var mHandler = Handler()
    protected var mDismissRunnable: Runnable = Runnable {
        mBaseMainView.showSplashScreen(false)
        mBaseMainView.startUIAnimations()
    }

    override fun handleOnLoad() {
        mBaseMainView.showSplashScreen(true)
        checkForUpdate()
    }

    private fun checkForUpdate() {
        OTAManager.checkAppUpdate(Utils.getAppVersionCode(BaseApplication.sInstance?.applicationContext), object : OTAManager.ForceUpdateListener {
            override fun onResponse(minVersion: Int, updateUrl: String) {
                Log.d(TAG, "App Min version:$minVersion")
                if (isAppNeedUpdate(minVersion)) {
                    mBaseMainView.showUpdateDialog(updateUrl)
                } else {
                    initAccount()
                }
            }

            override fun onError(exception: Exception?) {
                Log.e(TAG, exception?.localizedMessage)
                initAccount()
            }
        })
    }

    private fun initAccount() {

        val userAccount = AccountManagement.getInstance(BaseApplication.sInstance?.applicationContext).userAccount
        if (userAccount != null) {
            val accessToken = AccountManagement.getInstance(BaseApplication.sInstance?.applicationContext).userAccount.accessToken
            if (accessToken != null) {
                checkIfTokenValid(accessToken)
            } else {
                onEmptyToken()
            }
        } else {
            onEmptyToken()
        }
    }

    private fun onEmptyToken() {
        mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION)
        //check if the app is installed for first time
        val isFirstTimeInstalled = AccountManagement.getInstance(BaseApplication.sInstance?.applicationContext).getIsFirstTimeInstallation()
        if (isFirstTimeInstalled) {
            Log.d(TAG, "display welcome modal");
            mBaseMainView.showWelcomeModalDialog()
            AccountManagement.getInstance(BaseApplication.sInstance?.applicationContext).saveIsFirstTimeInstallation(false)
        }
    }

    private fun checkIfTokenValid(accessToken: String) {
        SproutlingApi.getTokenInfo(object : Callback<User> {
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                Log.e(TAG, t?.message)
            }

            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        var userObj = response.body()
                        if (userObj != null) {
//                            AccountManagement.getInstance(BaseApplication.sInstance).
//                                    writeToPreferences(userObj)
                            BaseApplication.sInstance?.initializeMqtt()
                            setUp()
                        }
                    } else {
                        mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION)
                    }
                } else {
                    mHandler.postDelayed(mDismissRunnable, SPLASH_DURATION)
                }
            }
        }, accessToken)
    }

    private fun setUp() {
        setUpMixPanelUserProfile()
        setUpPushNotification()
    }

    abstract fun setUpMixPanelUserProfile()

    override fun handleOnPushNotificationTokenRegistration() {
        getChildren()
    }

    private fun getChildren() {
        val accessToken = AccountManagement.getInstance(BaseApplication.sInstance?.applicationContext).userAccount.accessToken
        SproutlingApi.getChildrenList(object : Callback<ArrayList<Child>> {
            override fun onResponse(call: Call<ArrayList<Child>>?, response: Response<ArrayList<Child>>?) {
                if (response != null) {
                    if (response.isSuccessful) {
                        if (!response.body()!!.isEmpty()) {
                            val child: Child = response.body()!!.get(0)
                            AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).child = child
                        }
                        processChildrenListResponse(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Child>>?, t: Throwable?) {
                Log.e(TAG, t?.message)
            }
        }, accessToken)
    }

    abstract fun processChildrenListResponse(children: ArrayList<Child>?)

    private fun isAppNeedUpdate(appMinVersion: Int): Boolean {
        return Utils.getAppVersionCode(BaseApplication.sInstance?.applicationContext) < appMinVersion
    }

    override fun handleSignUpClick() {
        mBaseMainView.showLegalUI()
    }

    override fun handleSignInClick() {
        mBaseMainView.showSignInUI()
    }
}