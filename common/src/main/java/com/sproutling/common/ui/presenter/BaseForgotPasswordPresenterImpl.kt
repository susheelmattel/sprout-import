package com.sproutling.common.ui.presenter

import com.sproutling.common.ui.presenter.interfaces.IBaseForgotPasswordPresenter
import com.sproutling.common.ui.view.interfaces.IBaseForgotPasswordView

/**
 * Created by subram13 on 1/2/18.
 */
class BaseForgotPasswordPresenterImpl(baseForgotPasswordView: IBaseForgotPasswordView) : BasePresenterImpl(baseView = baseForgotPasswordView), IBaseForgotPasswordPresenter {
    override fun handlePinVerified(pin: String, userID: String, isEmail: Boolean) {
        mBaseForgotPasswordView.loadCreateNewPwd(pin, userID, isEmail)
        mBaseForgotPasswordView.enableActionBtn(false)
    }

    override fun handleResentPinSent(isEmail: Boolean, userID: String) {
        mBaseForgotPasswordView.loadPinVerificationFragment(isEmail, userID)
        mBaseForgotPasswordView.enableActionBtn(false)
    }

    override fun onForgotPasswordLoad() {
        mBaseForgotPasswordView.loadResetPwdCodeRequestFragment()
        mBaseForgotPasswordView.enableActionBtn(false)
    }

    private var mBaseForgotPasswordView = baseForgotPasswordView
}