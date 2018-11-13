package com.sproutling.common.ui.presenter.interfaces

/**
 * Created by subram13 on 1/5/18.
 */
interface ICreateNewPwdPresenter : IBaseFrgPresenter {

    fun handleOnPwdFocusChange(hasFocus: Boolean, pwdString: String)
    fun handleOnConfirmPwdFocusChange(hasFocus: Boolean, confPwdString: String)
    fun handlePwdTextChange(pwdString: String, confirmPwdString: String)
    fun handleConfirmPwdTextChange(confPwdString: String, pwdString: String)
    fun createNewPassword(userID: String, pin: String, password: String, confirmPassword: String, isEmail: Boolean)
    fun onPwdSuccessDialogClick()
}