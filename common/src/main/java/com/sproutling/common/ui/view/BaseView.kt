/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.common.ui.view

import android.app.Activity
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.pojos.events.PushNotificationChannelEvent
import com.sproutling.common.pojos.events.PushNotificationRegistrationEvent
import com.sproutling.common.ui.dialogfragment.BaseNetworkDialogFragment
import com.sproutling.common.ui.dialogfragment.GenericAlertDialogFragment
import com.sproutling.common.ui.dialogfragment.NetworkMessageDialog
import com.sproutling.common.ui.presenter.BasePresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IBasePresenter
import com.sproutling.common.ui.receivers.NetworkChangeReceiver
import com.sproutling.common.ui.view.interfaces.IBaseView
import com.sproutling.common.utils.CWLocationManager
import com.sproutling.common.utils.Utils
import com.sproutling.events.ServiceNetworkEvent
import com.sproutling.pojos.CreateHandheldResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by bradylin on 3/10/17.
 */

abstract class BaseView : AppCompatActivity(), IBaseView, BaseFragmentView.BaseFragmentListener {

    companion object {
        val FISHER_PRICE = "fisherPrice"
        val TAG = BaseView::class.java.simpleName
        val REQUEST_GPS_SETTINGS = 5487
        val REQUEST_CHECK_SETTINGS = 1612
        val NETWORK_RETRY_TIME: Long = 3000
        private var mNetworkMessageDialog: BaseNetworkDialogFragment? = null
    }


    protected var mToolbar: Toolbar? = null
    protected var mActionBar: ActionBar? = null
    private lateinit var mBasePresenter: IBasePresenter
    private lateinit var mProgressBar: ProgressDialog
    private var mNetworkChangeReceiver: NetworkChangeReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBasePresenter = BasePresenterImpl(this)
        mProgressBar = ProgressDialog(this)
        mProgressBar.setMessage(getString(R.string.progressbar_please_wait))

        mNetworkMessageDialog = BaseNetworkDialogFragment()

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.my_status_bar_color)
    }


    override fun showProgressBar(show: Boolean) {
        if (show) {
            hideKeyboard()
            if (mProgressBar != null) mProgressBar.show();
        } else {
            if (mProgressBar != null && mProgressBar.isShowing) {
                mProgressBar.dismiss()
            }
        }
    }

    protected fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    protected fun setUpPushNotification() {
        //        if (BuildConfig.FLAVOR_app.equalsIgnoreCase(FISHER_PRICE_CHINA_FLAVOR)) {
        //            //Baidu Push notification
        //            PushSettings.enableDebugMode(this, true);
        //            String pushApiKey = Utils.getMetaValue(this, "api_key");
        //            //start push notification
        //            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, pushApiKey);
        //            Log.v(TAG, "bdpush PushEnabled: " + PushManager.isPushEnabled(getApplicationContext()));
        //        } else {
        ////            FCM push notification
        //            // Get updated InstanceID token.
        //            String pushToken = FirebaseInstanceId.getInstance().getToken();
        //            Log.d(TAG, "Push token: " + pushToken);
        //            if (!TextUtils.isEmpty(pushToken)) {
        //                Utils.sendRegistrationToServer(this, pushToken);
        //            } else {
        //                onPushNotificationRegistrationFailure(null);
        //            }
        //        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(pushNotificationChannelEvent: PushNotificationChannelEvent) {
        Utils.sendRegistrationToServer(this, pushNotificationChannelEvent.channelId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(pushNotificationRegistrationEvent: PushNotificationRegistrationEvent) {
        if (pushNotificationRegistrationEvent.isSuccess) {
            onPushNotificationRegistrationSuccess(pushNotificationRegistrationEvent.createHandheldResponse)
        } else {
            onPushNotificationRegistrationFailure(pushNotificationRegistrationEvent.throwable)
        }
    }


    protected open fun onPushNotificationRegistrationSuccess(createHandheldResponse: CreateHandheldResponse?) {
        Log.e(TAG, "Success registering push notification")
    }

    protected open fun onPushNotificationRegistrationFailure(t: Throwable?) {
        Log.e(TAG, "Error registering push notification")
        //TODO: change the implementation once we figure how to handle failure case
        onPushNotificationRegistrationSuccess(null)
    }

    override fun onStart() {
        setUpActionBar()
        super.onStart()
        registerConnectivityReceiver()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    protected open fun setUpActionBar() {
        setUpToolbar()
        initActionBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mNetworkChangeReceiver)
        unRegisterEventBus()

    }

    protected fun unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    protected open fun initActionBar() {
        if (mToolbar != null) {
            mActionBar = supportActionBar
            assert(mActionBar != null)
            mActionBar!!.setDisplayHomeAsUpEnabled(true)
            mActionBar!!.setDisplayShowHomeEnabled(true)
            mActionBar!!.setDisplayShowTitleEnabled(false)
            setUpBackArrowColor()
            mToolbar!!.setNavigationOnClickListener { onBackPressed() }
        }

    }

    fun disableBack() {
        mActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    private fun setUpBackArrowColor() {
        val upArrow = getDrawable(R.drawable.abc_ic_ab_back_material)
        upArrow.setColorFilter(Utils.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
        mActionBar!!.setHomeAsUpIndicator(upArrow)
    }

    protected fun setUpToolbar() {
        mToolbar = findViewById(R.id.back_toolbar)
        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
        }
    }

    override fun setToolBarTitle(title: String) {
        if (mToolbar != null) {
            mToolbar!!.title = title
        } else {
            Log.d(TAG, "Toolbar is null")
        }
    }

    override fun enableActionBtn(enabled: Boolean) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    protected fun setBackDrawable(backDrawable: Int) {
        mActionBar!!.setHomeAsUpIndicator(backDrawable)
    }

    fun openActivity(viewToOpen: Class<out BaseView>, bundle: Bundle?, clearStack: Boolean) {
        val intent = Intent(this, viewToOpen)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (clearStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

    fun replaceFragmentView(fragment: Fragment, tag: String, addToBackStack: Boolean) {

        var fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment, tag)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag)
        }
        fragmentTransaction.commit()
        supportFragmentManager.executePendingTransactions()
    }

    fun showBluetoothDisabledDialog() {
        val bluetoothDialog = GenericAlertDialogFragment.newInstance(getString(R.string.bluetooth_screen_off), getString(R.string.bluetooth_screen_off_msg),
                getString(R.string.bluetooth_screen_cancel), getString(R.string.bluetooth_screen_turn_on))

        bluetoothDialog.setOnClickListener(object : GenericAlertDialogFragment.OnDialogActionListner {
            override fun onPositiveButtonClick() {

                if (Utils.enableBluetooth()) {
                    Utils.displayCustomToast(this@BaseView, getString(R.string.bluetooth_on))
                }
                if (!Utils.isLocationPermissionGranted(BaseApplication.sInstance!!.getAppContext())
                        || !Utils.isGPSEnabled(BaseApplication.sInstance!!.getAppContext())) {
                    showLocationDisabledDialog()
                }
            }

            override fun onNegativeButtonClick() {

            }
        })
        bluetoothDialog.show(fragmentManager, null)
    }

    open fun showLocationDisabledDialog() {
        var locationDialogTitle = getString(R.string.location_screen_off)
        var locationDialogMessage = getString(R.string.location_screen_off_msg)
        var locationDialogButton = getString(R.string.location_screen_turn_on)

        if (!Utils.isLocationPermissionGranted(this@BaseView)) {
            locationDialogTitle = getString(R.string.location_screen_access)
            locationDialogMessage = getString(R.string.location_screen_permission_message)
            locationDialogButton = getString(R.string.location_screen_enable)
        }

        val locationDialog = GenericAlertDialogFragment.newInstance(locationDialogTitle, locationDialogMessage,
                getString(R.string.location_screen_cancel), locationDialogButton)

        locationDialog.setOnClickListener(object : GenericAlertDialogFragment.OnDialogActionListner {
            override fun onPositiveButtonClick() {

                enableLocationServices()
            }

            override fun onNegativeButtonClick() {

            }
        })
        locationDialog.show(fragmentManager, null)
    }

    fun enableLocationServices() {

        if (Utils.isLocationPermissionGranted(this@BaseView) && !Utils.isGPSEnabled(this@BaseView)) {
            if (Utils.checkPlayServices(this@BaseView)) {
                CWLocationManager.enableLocationSettings(this@BaseView)
            } else {
                CWLocationManager.showLocationSettings(this@BaseView)
            }
        } else {
            CWLocationManager.requestLocationPermission(this@BaseView)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CWLocationManager.PERMISSION_LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Utils.displayCustomToast(this@BaseView, getString(R.string.location_permission_granted))
                    if (Utils.checkPlayServices(this@BaseView)) {
                        CWLocationManager.enableLocationSettings(this@BaseView)
                    } else {
                        CWLocationManager.showLocationSettings(this@BaseView)
                    }
                } else {
                    Utils.displayCustomToast(this@BaseView, getString(R.string.location_permission_denied))
                }
                return
            }
        }
    }

    open fun showNetworkDialog(showDialog: Boolean) {
        if (mNetworkMessageDialog != null) {
            supportFragmentManager.executePendingTransactions()

            var isShowing = false
            val isAdded = mNetworkMessageDialog?.isAdded
            if (mNetworkMessageDialog!!.dialog != null) {
                isShowing = mNetworkMessageDialog?.dialog?.isShowing!!
            }

            if (showDialog && !isShowing && !isAdded!!) {
                //dismiss the progress bar before showing another dialog fragment
                supportFragmentManager.executePendingTransactions()
                mNetworkMessageDialog?.show(supportFragmentManager, null)
            }
            if (!showDialog && isShowing && !isFinishing) {
                mNetworkMessageDialog?.dismiss()
                networkAvailable()
            }
        }
    }

    open fun networkAvailable() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(networkEvent: ServiceNetworkEvent) {
        showNetworkDialog(!networkEvent.networkStatus)
    }

    private fun registerConnectivityReceiver() {
        mNetworkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mNetworkChangeReceiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                Utils.displayCustomToast(this, getString(R.string.location_access_enabled))
            } else{
                Utils.displayCustomToast(this, getString(R.string.location_access_disabled))
            }
        }
    }
}
