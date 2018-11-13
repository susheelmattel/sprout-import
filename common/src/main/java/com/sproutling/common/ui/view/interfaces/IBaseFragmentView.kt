package com.sproutling.common.ui.view.interfaces



/**
 * Created by subram13 on 1/2/18.
 */
interface IBaseFragmentView {
    fun showProgressBar(show: Boolean)
    fun hideKeyboard()
    fun displayContactSupportDialog()
    fun makeSupportCall(mcallNumber:String)
    fun setNetworkMessageView()
    fun showGenericErrorDialog()
}