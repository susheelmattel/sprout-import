package com.sproutling.common.ui.presenter.interfaces

import com.sproutling.pojos.CreateUserRequestBody

/**
 * Created by subram13 on 1/9/18.
 */
interface IAccountCreationPresenter : IBaseFrgPresenter {
    fun onFirstNameChanged(firstName: String)
    fun onLastNameChanged(lastName: String)
    fun onEmailChanged(email: String)
    fun onPhoneChanged(phone: String)
    fun onPasswordChanged(password: String)

    fun onFirstNameFocusChanged(firstName: String, hasFocus: Boolean)
    fun onLastNameFocusChanged(lastName: String, hasFocus: Boolean)
    fun onEmailFocusChanged(email: String, hasFocus: Boolean)
    fun onPhoneFocusChanged(phone: String, hasFocus: Boolean)
    fun onPasswordFocusChanged(password: String, hasFocus: Boolean)

    fun createAccount(firstName: String, lastName: String, email: String, phone: String, password: String,
                      @CreateUserRequestBody.Type accountType: String, inviteToken: String, language: String, locale: String, sourceApp: String)
    fun updateAccount(firstName: String, lastName: String, email: String, phone: String, password: String)
    fun loadUserValues()

    fun handleOnStart(viewType:String)
    fun onPasswordChangeClick()
}