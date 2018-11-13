package com.sproutling.common.ui.view.interfaces

import com.sproutling.pojos.CreateUserResponse

/**
 * Created by subram13 on 1/9/18.
 */
interface IAccountCreationFragmentView : IBaseFragmentView {
    fun showFirstNameError(show: Boolean)
    fun showLastNameError(show: Boolean)
    fun showEmailError(show: Boolean, error: Int)
    fun showPhoneError(show: Boolean, error: Int)
    fun showPasswordError(show: Boolean, error: Int)
    fun showPasswordInfoMsg(show: Boolean)
    fun showPhoneInfoMsg(show: Boolean)
    fun enableActionBtn(enable: Boolean)
    fun onNextBtnClick()
    fun onSaveBtnClick()
    fun onUserLoggedIn()
    fun setUserInformation(userAccountInfo: CreateUserResponse, password: String)
    fun onUserInfoUpdated()
    fun setToolBarTitle(title:String)
    fun showResetPasswordScreen()

}