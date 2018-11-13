package com.sproutling.common.ui.presenter.interfaces

import android.os.Bundle

interface IBaseBLEFragmentPresenter : IBaseFrgPresenter {
    fun processBLEBroadcastMessage(action: String, data: Bundle)
}