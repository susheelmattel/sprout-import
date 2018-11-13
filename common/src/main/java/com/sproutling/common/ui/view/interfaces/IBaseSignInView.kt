package com.sproutling.common.ui.view.interfaces

/**
 * Created by subram13 on 12/14/17.
 */
interface IBaseSignInView : IBaseView {
    fun enableSignInButton(enable: Boolean)
    fun showForgotPasswordUI()
    fun showUpdateServerUrlDialog()
    fun showUserIDError(show: Boolean)
    fun showInvalidLoginDialog()
}