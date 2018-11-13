package com.sproutling.common.ui.view.interfaces

/**
 * Created by subram13 on 1/3/18.
 */
interface IPinVerificationFragmentView : IBaseFragmentView {
    fun onNextBtnClick()
    fun showInvalidVerificationCodeDialog()
    fun onPinVerified(pin: String, userID: String, isEmail: Boolean)
    fun showAlreadyPinSentError(show: Boolean)
    fun clearPin()
    fun enableActionBtn(enable: Boolean)
}