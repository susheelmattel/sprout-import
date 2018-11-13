package com.sproutling.common.ui.presenter.interfaces

import android.os.Bundle

/**
 * Created by 322511 on 8/1/18.
 */

interface IBaseOTAFragmentPresenter : IBaseBLEFragmentPresenter {
    fun processFirmwareBroadcastMessage(action: String, data: Bundle)
}