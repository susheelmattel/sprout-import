package com.sproutling.common.ui.presenter

import android.util.Log
import com.sproutling.api.SproutlingApi
import com.sproutling.common.R
import com.sproutling.common.ui.presenter.BaseSignInPresenterImpl.Companion.PASSWORD_MAX
import com.sproutling.common.ui.presenter.BaseSignInPresenterImpl.Companion.PASSWORD_MIN
import com.sproutling.common.ui.presenter.interfaces.ICreateNewPwdPresenter
import com.sproutling.common.ui.view.interfaces.ICreateNewPwdFragmentView
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.ResetPasswordRequestBody
import com.sproutling.pojos.ResetPasswordResponse
import com.sproutling.pojos.ResetPinRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by subram13 on 1/5/18.
 */
class CreateNewPwdPresenterImpl(createNewPwdFragmentView: ICreateNewPwdFragmentView) : BaseFrgPresenterImpl(createNewPwdFragmentView), ICreateNewPwdPresenter {
    override fun onPwdSuccessDialogClick() {
        mCreateNewPwdFragmentView.closeForgotPassword()
    }

    override fun createNewPassword(userID: String, pin: String, password: String, confirmPassword: String, isEmail: Boolean) {
        if (userID.isNotEmpty() && pin.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            val userIDVal = if (isEmail) userID else Utils.prefixCountryCode(userID)
            mCreateNewPwdFragmentView.showProgressBar(true)
            val resetPasswordRequestBody: ResetPasswordRequestBody
            resetPasswordRequestBody = if(isEmail){
                ResetPasswordRequestBody(userIDVal,null,  pin, password, confirmPassword)
            } else {
                ResetPasswordRequestBody(null, userIDVal, pin, password, confirmPassword)
            }


            SproutlingApi.resetPassword(resetPasswordRequestBody, object : Callback<ResetPasswordResponse> {
                override fun onResponse(call: Call<ResetPasswordResponse>?, response: Response<ResetPasswordResponse>?) {
                    if (response!!.isSuccessful) {
                        mCreateNewPwdFragmentView.showPwdChangeSuccessDialog()
                        Log.d(TAG, "Pwd change success")
                    } else {
                        Log.d(TAG, "Pwd change failed")
                    }
                }

                override fun onFailure(call: Call<ResetPasswordResponse>?, t: Throwable?) {
//                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    override fun handleOnPwdFocusChange(hasFocus: Boolean, pwdString: String) {
        if (!hasFocus) {
            mCreateNewPwdFragmentView.showPwdError(getPwdError(), !mIsPasswordValid)
        }
    }

    override fun handleOnConfirmPwdFocusChange(hasFocus: Boolean, confPwdString: String) {
        if (!hasFocus) {
            mCreateNewPwdFragmentView.showConfirmPwdError(R.string.set_new_password_do_not_match, !mIsConfirmPwdMatches)
        }
    }

    override fun handlePwdTextChange(pwdString: String, confPwdString: String) {
        validatePassword(pwdString)
        mIsConfirmPwdMatches = pwdString == confPwdString
        if (mIsPasswordValid) {
            mCreateNewPwdFragmentView.showPwdError(getPwdError(), !mIsPasswordValid)
        }
        mCreateNewPwdFragmentView.enableActionBtn(mIsPasswordValid && mIsConfirmPwdMatches)
    }

    override fun handleConfirmPwdTextChange(confPwdString: String, pwdString: String) {
        mIsConfirmPwdMatches = confPwdString == pwdString
        if (mIsConfirmPwdMatches) {
            mCreateNewPwdFragmentView.showConfirmPwdError(R.string.set_new_password_do_not_match, !mIsConfirmPwdMatches)
        }
        mCreateNewPwdFragmentView.enableActionBtn(mIsPasswordValid && mIsConfirmPwdMatches)
    }


    private fun getPwdError(): Int {
        var errorMsg = R.string.set_new_password_error_too_short
        if (!mIsPasswordValid && mPasswordFailState == PASSWORD_MAX) {
            errorMsg = R.string.set_new_password_error_too_long
        }
        return errorMsg
    }

    private fun validatePassword(password: String) {
        mIsPasswordValid = password.isNotEmpty() &&
                password.length >= PASSWORD_MIN &&
                password.length <= PASSWORD_MAX

        var length = password.length
        when {
            length < PASSWORD_MIN -> {
                mIsPasswordValid = false
                mPasswordFailState = PASSWORD_MIN
            }
            length > PASSWORD_MAX -> {
                mIsPasswordValid = false
                mPasswordFailState = PASSWORD_MAX
            }
            else -> mIsPasswordValid = true
        }
    }

    companion object {
        const val TAG = "CreateNewPwdPrsntrImpl"
    }

    private var mPasswordFailState = 0
    private var mIsConfirmPwdMatches = false
    private var mIsPasswordValid = false
    private var mCreateNewPwdFragmentView = createNewPwdFragmentView
}