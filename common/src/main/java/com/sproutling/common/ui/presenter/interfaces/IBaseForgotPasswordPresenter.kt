package com.sproutling.common.ui.presenter.interfaces

/**
 * Created by subram13 on 1/2/18.
 */
interface IBaseForgotPasswordPresenter : IBasePresenter {

    fun onForgotPasswordLoad()
    fun handleResentPinSent(isEmail: Boolean, userID: String)
    fun handlePinVerified(pin: String, userID: String, isEmail: Boolean)
}