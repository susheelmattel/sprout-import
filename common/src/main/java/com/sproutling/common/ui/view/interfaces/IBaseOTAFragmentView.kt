package com.sproutling.common.ui.view.interfaces

import android.content.IntentFilter
import com.sproutling.common.ui.view.interfaces.IBaseBLEFragmentView
import com.sproutling.common.ui.view.interfaces.IBaseFragmentView

interface IBaseOTAFragmentView : IBaseBLEFragmentView {
    fun getFirmwareFilters(): IntentFilter?
}
