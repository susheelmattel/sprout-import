package com.sproutling.common.ui.presenter.interfaces

/**
 * Created by subram13 on 12/14/17.
 */
interface IBaseMainPresenter : IBasePresenter {
    fun handleSignUpClick()
    fun handleSignInClick()
    fun handleOnLoad()
    fun handleOnUpdateClick(url: String)
    fun handleOnPushNotificationTokenRegistration()
}