package com.sproutling.common.ui.view

import android.os.Bundle
import android.view.View
import com.sproutling.common.R
import com.sproutling.common.ui.presenter.BaseLegalPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IBaseLegalPresenter
import com.sproutling.common.ui.view.interfaces.IBaseLegalView

/**
 * Created by subram13 on 1/9/18.
 */
abstract class BaseLegalView : BaseView(), IBaseLegalView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseLegalPresenter = BaseLegalPresenterImpl(this)

    }

    override fun onStart() {
        super.onStart()
        setToolBarTitle(getString(R.string.legal_important))
    }

    val mAcceptClickListener = View.OnClickListener { mBaseLegalPresenter.onAcceptClick() }
    private lateinit var mBaseLegalPresenter: IBaseLegalPresenter
}