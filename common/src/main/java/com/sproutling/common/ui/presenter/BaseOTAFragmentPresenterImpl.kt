package com.sproutling.common.ui.presenter

import android.os.Bundle
import com.sproutling.common.ui.presenter.BaseFrgPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IBaseOTAFragmentPresenter
import com.sproutling.common.ui.view.interfaces.IBaseOTAFragmentView

/**
 * Created by 322511 on 7/31/18.
 */

abstract class BaseOTAFragmentPresenterImpl(baseFirmwareFragmentView: IBaseOTAFragmentView) : BaseBLEFragmentPresenterImpl(baseFirmwareFragmentView), IBaseOTAFragmentPresenter {

        private var mBaseFirmwareFragmentView = baseFirmwareFragmentView
}

