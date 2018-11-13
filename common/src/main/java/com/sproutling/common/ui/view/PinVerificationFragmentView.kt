package com.sproutling.common.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sproutling.common.R
import com.sproutling.common.ui.dialogfragment.InvalidVerificationCodeDialogFragment
import com.sproutling.common.ui.presenter.PinVerificationPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IPinVerificationPresenter
import com.sproutling.common.ui.view.interfaces.IPinVerificationFragmentView
import com.sproutling.common.utils.Utils
import kotlinx.android.synthetic.main.fragment_pin_verification.*

/**
 * Created by subram13 on 1/3/18.
 */
class PinVerificationFragmentView : BaseFragmentView(), IPinVerificationFragmentView {
    override fun enableActionBtn(enable: Boolean) {
        (activity as PinVerifyListener).enableActionBtn(enable)
    }

    override fun clearPin() {
        code.setText("", TextView.BufferType.EDITABLE)
    }

    override fun showAlreadyPinSentError(show: Boolean) {
        code.showErrorMsg(show)
    }

    override fun onPinVerified(pin: String, userID: String, isEmail: Boolean) {
        if(activity != null){
            (activity as PinVerifyListener).onCodeAccepted(pin = pin, userID = userID, isEmail = isEmail)
        }
    }

    override fun showInvalidVerificationCodeDialog() {
        InvalidVerificationCodeDialogFragment().show(fragmentManager, null)
    }

    override fun onNextBtnClick() {
        mPinVerificationPresenter.verifyPin(pin = code.text.toString(), userID = mUserID, isEmail = mIsEmail)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_pin_verification, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mIsEmail = arguments.getBoolean(IS_EMAIL, false)
        mUserID = arguments.getString(USER_ID)

        titleInstruction.text = if (mIsEmail) {
            String.format(getString(R.string.sms_verification_message_email), Utils.getAppName())
        } else {
            String.format(getString(R.string.sms_verification_message_phone), Utils.getAppName())
        }
        instruction.text = String.format(getString(R.string.sms_verification_phone_instruction), mUserID)
        code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPinVerificationPresenter.onPinTextChange(s.toString())
            }
        })
        resendCode.setOnClickListener { mPinVerificationPresenter.resendPin(mIsEmail, mUserID) }

        mPinVerificationPresenter = PinVerificationPresenterImpl(this)

    }

    override fun onStart() {
        super.onStart()
        (activity as PinVerifyListener).setTitle(getString(R.string.sms_verification_code))
        (activity as PinVerifyListener).enableActionBtn(false)
    }

    internal interface PinVerifyListener {
        fun enableActionBtn(enabled: Boolean)
        fun onCodeAccepted(pin: String, userID: String, isEmail: Boolean)
        fun setTitle(title: String)
    }

    private lateinit var mPinVerificationPresenter: IPinVerificationPresenter
    private var mIsEmail = false
    private lateinit var mUserID: String

    companion object {
        const val IS_EMAIL = "IS_EMAIL"
        const val USER_ID = "USER_ID"
        const val TAG = "PinVerificationFragmentView"

        fun instance(userID: String, isEmail: Boolean): PinVerificationFragmentView {
            val bundle = Bundle()
            bundle.putString(USER_ID, userID)
            bundle.putBoolean(IS_EMAIL, isEmail)
            val fragment = PinVerificationFragmentView()
            fragment.arguments = bundle
            return fragment
        }
    }
}