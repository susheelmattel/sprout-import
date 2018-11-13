package com.sproutling.common.ui.view.interfaces

import android.content.IntentFilter
import com.sproutling.common.ble.Constants

interface IBaseBLEFragmentView : IBaseFragmentView {
    fun getBLEFilters(): IntentFilter? {
        val bleIntentFilter = IntentFilter()
        bleIntentFilter.addAction(Constants.Filter.BLE_NEW_PERIPHERAL)
        bleIntentFilter.addAction(Constants.Filter.BLE_CONNECTION_CHANGED)
        bleIntentFilter.addAction(Constants.Filter.BLE_MODEL_UPDATED)
        return bleIntentFilter
    }
}
