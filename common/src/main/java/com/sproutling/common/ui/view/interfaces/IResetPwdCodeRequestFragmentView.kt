package com.sproutling.common.ui.view.interfaces

/**
 * Created by subram13 on 1/2/18.
 */
interface IResetPwdCodeRequestFragmentView : IBaseFragmentView {


    fun showUserIDFormatError(show: Boolean)
    fun showEmailIDFormatError(show: Boolean)
    fun showInvalidUserIDError(show: Boolean)
    fun enableSaveBtn(enable: Boolean, userID: String)
    fun onSaveBtnClick()
    fun onResetPinSent(isEmail: Boolean, userID: String)
}