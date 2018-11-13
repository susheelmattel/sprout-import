package com.sproutling.common.ui.presenter.interfaces

/**
 * Created by subram13 on 12/14/17.
 */
interface IBaseSignInPresenter : IBasePresenter {
    fun onSignInClicked(userID: String, password: String)
    fun onForgotPasswordClicked()
    fun onUpdateServerUrlClicked()
    fun validateUserID(userID: String)
    fun validatePassword(password: String)
    fun handleUserIDFocusChange(hasFocus: Boolean, userID: String)
    fun handleOnPushNotificationIDRegistration(success: Boolean)
}