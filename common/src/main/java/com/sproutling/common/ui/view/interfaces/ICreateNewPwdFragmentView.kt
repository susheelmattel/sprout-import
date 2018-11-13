package com.sproutling.common.ui.view.interfaces

/**
 * Created by subram13 on 1/5/18.
 */
interface ICreateNewPwdFragmentView : IBaseFragmentView {
    fun enableActionBtn(enabled: Boolean)
    fun showPwdError(error: Int, show: Boolean)
    fun showConfirmPwdError(error: Int, show: Boolean)
    fun onNextBtnClick()
    fun showPwdChangeSuccessDialog()
    fun closeForgotPassword()
}