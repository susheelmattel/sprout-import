package com.sproutling.common.ui.view.interfaces

/**
 * Created by subram13 on 12/14/17.
 */
interface IBaseMainView : IBaseView {

    fun showLegalUI()
    fun showSignInUI()
    fun startUIAnimations()
    fun showSplashScreen(show: Boolean)
    fun showUpdateDialog(updateUrl: String)
    fun openUpdateUrl(url: String)
    fun showWelcomeModalDialog()
}