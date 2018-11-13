package com.sproutling.ui.fragment.interfaces

/**
 * Created by subram13 on 11/3/17.
 */
interface IBaseFragment {
    fun onBackPressed()
    fun showAlertDialog(title: String, message: String, buttonText: String, imageRes: Int)
    fun showProgressBar(show: Boolean)
}