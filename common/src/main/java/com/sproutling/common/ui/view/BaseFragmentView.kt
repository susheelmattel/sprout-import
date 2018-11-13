package com.sproutling.common.ui.view


import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.pojos.events.DummyEvent
import com.sproutling.common.ui.dialogfragment.ContactSupportDialogFragment
import com.sproutling.common.ui.dialogfragment.GenericErrorDialogFragment
import com.sproutling.common.ui.dialogfragment.NetworkMessageDialog
import com.sproutling.common.ui.presenter.BaseFrgPresenterImpl
import com.sproutling.common.ui.view.interfaces.IBaseFragmentView
import com.sproutling.common.utils.Utils
import com.sproutling.common.utils.Utils.PERMISSION_REQUEST_CALL
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by subram13 on 1/2/18.
 */
open class BaseFragmentView : Fragment(), IBaseFragmentView {
    private lateinit var mProgressBar: ProgressDialog
    private var mBaseFrgPresenter: BaseFrgPresenterImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseFrgPresenter = BaseFrgPresenterImpl(this)
        mProgressBar = ProgressDialog(activity)
        mProgressBar.setCancelable(false)
        mProgressBar.setCanceledOnTouchOutside(false)
        mProgressBar.setMessage(getString(R.string.progressbar_please_wait))
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        registerEventBus()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterEventBus()
    }


    override fun showProgressBar(show: Boolean) {
        if(isAdded && !activity.isFinishing){
            if (mProgressBar != null) {
                if (show) {
                    hideKeyboard()
                    mProgressBar.show()
                } else {
                    mProgressBar.dismiss()
                }
            }
        }
    }


    override fun setNetworkMessageView() {
     val networkDialog = NetworkMessageDialog()
        networkDialog.setOnClickListener(View.OnClickListener {
            networkDialog.dismiss()
        })
        networkDialog.show(fragmentManager, null)
    }

    override fun hideKeyboard() {
        val activity = activity ?: return
        val view = activity.currentFocus
        if (view != null) {
            if(view.hasFocus()){
                view.clearFocus()
            }
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun displayContactSupportDialog() {
        val fragment = ContactSupportDialogFragment()
        fragment.show(fragmentManager, ContactSupportDialogFragment.TAG)

        fragment.setOnClickListener(View.OnClickListener {
            if (Utils.isCallPermissionGranted(context)) {
                mBaseFrgPresenter?.handleCallClick()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL)
            }
            fragment.dismiss()
        })
        fragment.setOnNegativeClickListener(View.OnClickListener { fragment.dismiss() })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CALL -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mBaseFrgPresenter?.handleCallClick()
            }
        }
    }

    fun registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    fun unRegisterEventBus() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun dummyEvent(event: DummyEvent) {
    }

    @SuppressLint("MissingPermission")
    override fun makeSupportCall(callNumber: String) {
        val uri = "tel:" + callNumber.trim { it <= ' ' }
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse(uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        BaseApplication.sInstance!!.applicationContext.startActivity(intent)
    }

    interface BaseFragmentListener {
        fun enableActionBtn(enabled: Boolean)
        fun setToolBarTitle(title: String)
    }

    override fun showGenericErrorDialog() {
        GenericErrorDialogFragment().show(fragmentManager, null)
    }
}