package com.sproutling.common.ui.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.sproutling.common.BuildConfig
import com.sproutling.common.R
import com.sproutling.common.ui.dialogfragment.InvalidLoginDialogFragment
import com.sproutling.common.ui.presenter.interfaces.IBaseSignInPresenter
import com.sproutling.common.ui.view.interfaces.IBaseSignInView
import com.sproutling.pojos.CreateHandheldResponse
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_environment_switch.view.*
import java.util.*

/**
 * Created by subram13 on 12/14/17.
 */
abstract class BaseSignInView : BaseView(), IBaseSignInView {

    override fun showInvalidLoginDialog() {
        InvalidLoginDialogFragment().show(supportFragmentManager, null)
    }

    override fun showUserIDError(show: Boolean) {
        userID.showErrorMsg(show)
    }

    override fun showForgotPasswordUI() {
        openActivity(BaseForgotPasswordView::class.java, null, false)
    }

    override fun showUpdateServerUrlDialog() {
        showEnvironmentSwitchDialog()
    }

    override fun enableSignInButton(enable: Boolean) {
        btnSignIn.isEnabled = enable
    }

    override fun onPushNotificationRegistrationSuccess(createHandheldResponse: CreateHandheldResponse?) {
        Log.d(TAG, "onPushNotificationRegistrationSuccess")
        mBaseSignInPresenter.handleOnPushNotificationIDRegistration(true)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_login)
        btnSignIn.setOnClickListener { mBaseSignInPresenter.onSignInClicked(userID = userID.text.toString(), password = password.text.toString()) }
        forgotPassword.setOnClickListener { mBaseSignInPresenter.onForgotPasswordClicked() }
        tv_update_url.setOnClickListener { mBaseSignInPresenter.onUpdateServerUrlClicked() }
        if (BuildConfig.DEBUG) {
            tv_update_url.visibility = View.VISIBLE
        } else {
            tv_update_url.visibility = View.GONE
        }
        userID.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBaseSignInPresenter.validateUserID(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        userID.addTextChangedListener(PhoneNumberFormattingTextWatcher(Locale.getDefault().country))

        userID.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            mBaseSignInPresenter.handleUserIDFocusChange(hasFocus, userID = userID.text.toString())
        }
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBaseSignInPresenter.validatePassword(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        enableSignInButton(false)
        mBaseSignInPresenter = getSignInPresenter()

    }

    override fun onStart() {
        super.onStart()
        setToolBarTitle(getString(R.string.login_title))
    }

    private fun showEnvironmentSwitchDialog() {
        var builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(this)
        } else {
            builder = AlertDialog.Builder(this)
        }
        var view = layoutInflater.inflate(R.layout.dialog_environment_switch, null)
        var etServerUrl = view.et_server_url
        val etMqttServerUrl = view.et_mqtt_url
//        etServerUrl.setText(SSManagement.ENDPOINT)
//        etMqttServerUrl.setText(MqttAPI.MQTT_TLS_BROKER)
        builder.setView(view)
        builder.setPositiveButton(R.string.login_save, null)

        builder.setNegativeButton(R.string.login_cancel, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        val mAlertDialog = builder.create()
        mAlertDialog.setOnShowListener { dialog ->
            val positiveBtn = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveBtn.setOnClickListener {
                dialog.dismiss()
//                val serverUrl = etServerUrl.getText().toString()
//                val mqttUrl = etMqttServerUrl.getText().toString()
//                var errorMessage: String? = null
//                if (!URLUtil.isValidUrl(serverUrl)) {
//                    errorMessage = "Invalid Server Url format."
//                }
//                if (TextUtils.isEmpty(errorMessage)) {
//                    SSManagement.ENDPOINT = serverUrl
//                    MqttAPI.MQTT_TLS_BROKER = mqttUrl
//                    SharedPrefManager.saveServerUrl(this@LoginActivity, SSManagement.ENDPOINT)
//                    SharedPrefManager.saveMqttUrl(this@LoginActivity, MqttAPI.MQTT_TLS_BROKER)
//                    SproutlingApi.updateServerUrl(SSManagement.ENDPOINT)

//                } else {
//                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
//                }
            }
        }
        mAlertDialog.show()
    }

    abstract fun getSignInPresenter(): IBaseSignInPresenter

    private lateinit var mBaseSignInPresenter: IBaseSignInPresenter
}