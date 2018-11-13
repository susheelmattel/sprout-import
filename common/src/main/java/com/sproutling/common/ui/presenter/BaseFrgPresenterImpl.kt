package com.sproutling.common.ui.presenter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ui.presenter.interfaces.IBaseFrgPresenter
import com.sproutling.common.ui.view.interfaces.IBaseFragmentView
import com.sproutling.common.utils.Utils

/**
 * Created by subram13 on 1/3/18.
 */
open class BaseFrgPresenterImpl(baseFragmentView: IBaseFragmentView) : IBaseFrgPresenter {
    override fun handleCallClick() {
        mbaseFragmentView.makeSupportCall(mcallNumber)
    }

    private var mcallNumber = Utils.getString(R.string.call_number)
    private var mbaseFragmentView = baseFragmentView
}