package com.sproutling.common.ui.view

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fisherprice.api.models.FPSmartModel
import com.fisherprice.smartconnect.api.constants.FPBLEConstants
import com.sproutling.common.R
import com.sproutling.common.ble.Constants
import com.sproutling.common.ui.dialogfragment.FirmwareMandatoryUpdateDialogFragment
import com.sproutling.common.ui.presenter.BaseBLEPairingFragmentPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IBaseBLEFragmentPresenter
import com.sproutling.common.ui.presenter.interfaces.IBaseBLEPairingFragmentPresenter
import com.sproutling.common.ui.view.interfaces.IBaseBLEPairingFragmentView

abstract class BaseBLEPairingFragmentView : BaseBLEFragmentView(), IBaseBLEPairingFragmentView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseBLEPairingFragmentPresenter.setPeripheralType(getPeripheralType())
        mBaseBLEPairingFragmentPresenter.startBLEScanning()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            mIsPairingAgain = arguments.getBoolean(PAIRING_AGAIN, false)
        }
        initViews(view!!)
    }

    override fun onStart() {
        super.onStart()
        (activity as BLEPairingListener).setToolBarTitle(getToolbarTitle())
        (activity as BLEPairingListener).enableBackNavigation(true)
    }

    override fun getBaseBLEFragmentPresenter(): IBaseBLEFragmentPresenter {
        mBaseBLEPairingFragmentPresenter = BaseBLEPairingFragmentPresenterImpl(this)
        return mBaseBLEPairingFragmentPresenter
    }

    override fun showPairingCompletedScreen(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE, serialID: String) {
        (activity as BLEPairingListener).onPeripheralConnected(peripheralType, serialID)
    }

    override fun goToFirmwareUpdateScreen(fpModel: FPSmartModel<*>) {
        (activity as BLEPairingListener).onFirmwareStartClicked(fpModel)
    }

    override fun showFirmwareUpdateDialog(fpModel: FPSmartModel<*>) {
        val firmwareMandatoryUpdateDialogFragment = FirmwareMandatoryUpdateDialogFragment()
        firmwareMandatoryUpdateDialogFragment.setOnClickListener(View.OnClickListener {
            firmwareMandatoryUpdateDialogFragment.dismiss()
            mBaseBLEPairingFragmentPresenter.onStartUpdateClicked(fpModel)
        })

        firmwareMandatoryUpdateDialogFragment.show(activity.supportFragmentManager, BaseView.TAG)
    }

    private fun getToolbarTitle(): String {
        return if (!mIsPairingAgain) getString(R.string.pairing_title) else getString(R.string.pair_again)
    }

    protected abstract fun getPeripheralType(): FPBLEConstants.CONNECT_PERIPHERAL_TYPE
    protected abstract fun initViews(view: View)
    protected abstract fun getLayout(): Int

    private var mIsPairingAgain = false

    interface BLEPairingListener : BaseFragmentListener {
        fun onPeripheralConnected(peripheralType: FPBLEConstants.CONNECT_PERIPHERAL_TYPE, deviceSerialID: String)
        fun onFirmwareStartClicked(fpModel: FPSmartModel<*>)
        fun enableBackNavigation(enable: Boolean)
    }

    companion object {
        const val PAIRING_AGAIN = "PAIRING_AGAIN"
    }

    private lateinit var mBaseBLEPairingFragmentPresenter: IBaseBLEPairingFragmentPresenter
}