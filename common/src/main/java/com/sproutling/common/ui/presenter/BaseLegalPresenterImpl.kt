package com.sproutling.common.ui.presenter

import com.sproutling.common.ui.presenter.interfaces.IBaseLegalPresenter
import com.sproutling.common.ui.view.interfaces.IBaseLegalView

/**
 * Created by subram13 on 1/9/18.
 */
class BaseLegalPresenterImpl(baseLegalView: IBaseLegalView) : BasePresenterImpl(baseLegalView), IBaseLegalPresenter {
    override fun onAcceptClick() {
        mLegalView.showSignUpUI()
    }

    private var mLegalView = baseLegalView
}