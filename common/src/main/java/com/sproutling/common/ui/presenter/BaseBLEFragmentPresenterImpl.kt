package com.sproutling.common.ui.presenter

import com.sproutling.common.ui.presenter.interfaces.IBaseBLEFragmentPresenter
import com.sproutling.common.ui.view.interfaces.IBaseBLEFragmentView

abstract class BaseBLEFragmentPresenterImpl(baseBLEFragmentView: IBaseBLEFragmentView) : BaseFrgPresenterImpl(baseBLEFragmentView), IBaseBLEFragmentPresenter {
    private var mBaseBLEFragmentView = baseBLEFragmentView

}