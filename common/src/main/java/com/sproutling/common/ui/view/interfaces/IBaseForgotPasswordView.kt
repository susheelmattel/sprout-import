package com.sproutling.common.ui.view.interfaces

/**
 * Created by subram13 on 1/2/18.
 */
interface IBaseForgotPasswordView : IBaseView {
    fun loadResetPwdCodeRequestFragment()
    fun loadPinVerificationFragment(isEmail: Boolean, userID: String)
    fun loadCreateNewPwd(pin: String, userID: String, isEmail: Boolean)
    fun enableActionBtn(enable: Boolean)
}