package com.sproutling.common.ui.presenter

import android.widget.Toast
import com.sproutling.api.SproutlingApi
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ui.presenter.interfaces.IPinVerificationPresenter
import com.sproutling.common.ui.view.interfaces.IPinVerificationFragmentView
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.ResetPinRequestBody
import com.sproutling.pojos.ResetPinResponse
import com.sproutling.pojos.ValidatePinRequestBody
import com.sproutling.pojos.ValidatePinResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by subram13 on 1/3/18.
 */
class PinVerificationPresenterImpl(iPinVerificationFragmentView: IPinVerificationFragmentView) : BaseFrgPresenterImpl(iPinVerificationFragmentView), IPinVerificationPresenter {
    override fun onPinTextChange(pin: String) {
        mPinVerificationFragmentView.enableActionBtn(pin!!.length == PIN_LENGTH)
        mPinVerificationFragmentView.showAlreadyPinSentError(false)
    }

    override fun verifyPin(pin: String, userID: String, isEmail: Boolean) {
        mPinVerificationFragmentView.showProgressBar(true)
        if (pin.isNotEmpty() && userID.isNotEmpty() && !mPinVerifyLock) {
            mPinVerifyLock = true
            val userIDVal = if (isEmail) userID else Utils.prefixCountryCode(userID)
            mPinVerificationFragmentView.showAlreadyPinSentError(false)
            val validatePinRequestBody: ValidatePinRequestBody
            validatePinRequestBody = if (isEmail) {
                ValidatePinRequestBody(null, userIDVal, pin)
            } else {
                ValidatePinRequestBody(userIDVal, null, pin)
            }
            SproutlingApi.validatePin(validatePinRequestBody, object : Callback<ValidatePinResponse> {
                override fun onFailure(call: Call<ValidatePinResponse>?, t: Throwable?) {
                    mPinVerificationFragmentView.showGenericErrorDialog()
                    mPinVerificationFragmentView.showProgressBar(false)
                    mPinVerifyLock = false
                }

                override fun onResponse(call: Call<ValidatePinResponse>?, response: Response<ValidatePinResponse>?) {
                    mPinVerificationFragmentView.showProgressBar(false)
                    mPinVerifyLock = false
                    if (response!!.isSuccessful) {
                        mPinVerificationFragmentView.onPinVerified(pin = pin, userID = userID, isEmail = isEmail)
                    } else {
                        if (response.code() == 400) {
                            mPinVerificationFragmentView.clearPin()
                            mPinVerificationFragmentView.showInvalidVerificationCodeDialog()
                        } else {
                            mPinVerificationFragmentView.showGenericErrorDialog()
                        }

                    }
                }

            })
        }
    }

    override fun resendPin(isEmail: Boolean, userID: String) {
        if (Utils.isUserIdValid(userID)) {
            val isPhone = Utils.isPhoneValid(userID)
            val userIDVal = if (isPhone) Utils.prefixCountryCode(userID) else userID
            mPinVerificationFragmentView.showProgressBar(true)
            mPinVerificationFragmentView.showAlreadyPinSentError(false)
            val requestResetPinRequestBody: ResetPinRequestBody
            requestResetPinRequestBody = if (isPhone) {
                ResetPinRequestBody(userIDVal, null)
            } else {
                ResetPinRequestBody(null, userIDVal)
            }

            SproutlingApi.requestResetPin(requestResetPinRequestBody, object : Callback<ResetPinResponse> {
                override fun onFailure(call: Call<ResetPinResponse>?, t: Throwable?) {
                    mPinVerificationFragmentView.showProgressBar(false)
                    mPinVerificationFragmentView.showGenericErrorDialog()

                }

                override fun onResponse(call: Call<ResetPinResponse>?, response: Response<ResetPinResponse>?) {
                    mPinVerificationFragmentView.showProgressBar(false)
                    if (response!!.isSuccessful) {
                        if (response.code() == 200) {
                            Toast.makeText(BaseApplication.sInstance, R.string.sms_verification_pin_sent_successfully, Toast.LENGTH_LONG).show()
                        } else if (response.code() == 202) {
                            mPinVerificationFragmentView.showAlreadyPinSentError(response.body()!!.isPinAlreadySent)
                        } else {
                            mPinVerificationFragmentView.showGenericErrorDialog()
                        }
                    }
                }
            })
        }
    }

    companion object {
        const val PIN_LENGTH = 5
    }

    private var mPinVerificationFragmentView = iPinVerificationFragmentView
    private var mPinVerifyLock: Boolean = false;
}