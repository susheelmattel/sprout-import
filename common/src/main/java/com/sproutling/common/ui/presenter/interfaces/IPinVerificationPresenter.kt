package com.sproutling.common.ui.presenter.interfaces

/**
 * Created by subram13 on 1/4/18.
 */
interface IPinVerificationPresenter : IBaseFrgPresenter {
    fun resendPin(isEmail: Boolean, userID: String)
    fun verifyPin(pin: String, userID: String, isEmail: Boolean)
    fun onPinTextChange(pin: String)
}