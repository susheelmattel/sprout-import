package com.sproutling.common.ui.presenter.interfaces

/**
 * Created by subram13 on 1/2/18.
 */
interface IResetPwdCodeRequestPresenter : IBasePresenter {
    fun validateUserID(userID: String)
    fun handleSendBtnClick(userID: String)
}