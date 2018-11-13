package com.sproutling.common.ui.presenter

import com.sproutling.api.SproutlingApi
import com.sproutling.common.ui.presenter.interfaces.IResetPwdCodeRequestPresenter
import com.sproutling.common.ui.view.interfaces.IResetPwdCodeRequestFragmentView
import com.sproutling.common.utils.CommonConstant
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.ResetPinRequestBody
import com.sproutling.pojos.ResetPinResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by subram13 on 1/2/18.
 */
class ResetPwdCodeRequestPresenterImpl(resetPwdCodeRequestFragmentView: IResetPwdCodeRequestFragmentView) :
        BaseFrgPresenterImpl(resetPwdCodeRequestFragmentView), IResetPwdCodeRequestPresenter {
    override fun handleSendBtnClick(userID: String) {
        if (Utils.isUserIdValid(userID)) {
            val isPhone = Utils.isPhoneValid(userID)
            val userIDVal = if (isPhone) Utils.prefixCountryCode(userID) else userID
            mResetPwdCodeRequestFragmentView.showInvalidUserIDError(false)
            mResetPwdCodeRequestFragmentView.showProgressBar(true)
            val requestResetPinRequestBody: ResetPinRequestBody
            requestResetPinRequestBody = if (isPhone) {
                ResetPinRequestBody(userIDVal, null)
            } else {
                ResetPinRequestBody(null, userIDVal)
            }

            SproutlingApi.requestResetPin(requestResetPinRequestBody, object : Callback<ResetPinResponse> {
                override fun onFailure(call: Call<ResetPinResponse>?, t: Throwable?) {
                    mResetPwdCodeRequestFragmentView.showProgressBar(false)
                    mResetPwdCodeRequestFragmentView.showGenericErrorDialog()
                }

                override fun onResponse(call: Call<ResetPinResponse>?, response: Response<ResetPinResponse>?) {
                    mResetPwdCodeRequestFragmentView.showProgressBar(false)
                    if (response!!.isSuccessful) {
                        if (response.code() == CommonConstant.RESPONSE_CODE_200) {
                            mResetPwdCodeRequestFragmentView.onResetPinSent(!isPhone, userID)
                        } else if (response.code() == CommonConstant.RESPONSE_CODE_202) {
                            mResetPwdCodeRequestFragmentView.showInvalidUserIDError(response.body()!!.isUserIDWrong)
                            if (response.body()!!.isPinAlreadySent) {
                                mResetPwdCodeRequestFragmentView.onResetPinSent(!isPhone, userID)
                            }
                        }
                    } else {
                        mResetPwdCodeRequestFragmentView.showGenericErrorDialog()
                        if (response.code() == CommonConstant.ERROR_CODE_422) {
                            mResetPwdCodeRequestFragmentView.showInvalidUserIDError(true)
                        }
                    }
                }
            })
        }
    }

    /*Temporary change in validation of USERID; Uncomment the commented code and remove the email part*/
    override fun validateUserID(userID: String) {
        val isValid = Utils.isEmailIdValid(userID)
        mResetPwdCodeRequestFragmentView.showEmailIDFormatError(!isValid)
        //val isValid = Utils.isUserIdValid(userID)
        //mResetPwdCodeRequestFragmentView.showUserIDFormatError(!isValid)
        mResetPwdCodeRequestFragmentView.enableSaveBtn(isValid, userID)
    }

    companion object {
        const val TAG = "ResetPwdRqstPrsntrImpl"
    }

    private var mResetPwdCodeRequestFragmentView = resetPwdCodeRequestFragmentView
}