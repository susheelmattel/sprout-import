package com.sproutling.common.ui.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import com.sproutling.common.R
import kotlinx.android.synthetic.main.fragment_pairing_complete.*

abstract class BaseBLEPairingConnectedFragmentView : BaseFragmentView() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_pairing_complete, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        deviceImg.setImageDrawable(getDeviceImage())
        connectionMessage.text = String.format(getString(R.string.device_connected_body), getDeviceDisplayName())
        connectionNote.text = getNoteMessage()
    }

    override fun onStart() {
        super.onStart()
        (activity as BaseFragmentListener).setToolBarTitle(getString(R.string.device_connected_title))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (inflater == null) {
            Log.e(TAG, "inflater is null")
        }
        inflater?.inflate(R.menu.menu_one_action, menu)
        if (menu == null) {
            Log.e(TAG, "menu is null")
        }
        val menuItemNext = menu?.getItem(0)
        if (menuItemNext == null) {
            Log.e(TAG, "menuItemSave is null")
        }
        menuItemNext?.title = getString(R.string.account_creation_next)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        (activity as ConnectionNextListener).onConnectedNextClicked()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Override this method to return set the note message
     */
    protected fun getNoteMessage(): String {
        return getString(R.string.empty)
    }

    interface ConnectionNextListener {
        fun onConnectedNextClicked()
    }

    abstract fun getDeviceDisplayName(): String
    abstract fun getDeviceImage(): Drawable?

    companion object {
        const val TAG = "BseBLEPargContedFrgmtVw"
    }
}