package com.sproutling.common.ui.presenter

import com.sproutling.common.ui.presenter.interfaces.IAccountSetupPresenter
import com.sproutling.common.ui.view.interfaces.IAccountSetupView

/**
 * Created by subram13 on 1/9/18.
 */
abstract class AccountSetupPresenterImpl(accountSetupView: IAccountSetupView) : BasePresenterImpl(accountSetupView), IAccountSetupPresenter {

    override fun onAccountCreated() {
        mAccountSetupView.loadAddChildFragment()
    }

    override fun onAccountSetUpLoad() {
        mAccountSetupView.loadAccountCreationFragment()
    }


    private val mAccountSetupView = accountSetupView
}