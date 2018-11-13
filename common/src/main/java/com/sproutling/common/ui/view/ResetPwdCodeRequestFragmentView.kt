package com.sproutling.common.ui.view

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sproutling.common.R
import com.sproutling.common.ui.dialogfragment.GenericErrorDialogFragment
import com.sproutling.common.ui.presenter.ResetPwdCodeRequestPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IResetPwdCodeRequestPresenter
import com.sproutling.common.ui.view.interfaces.IResetPwdCodeRequestFragmentView
import kotlinx.android.synthetic.main.fragment_forgot_password_enter_phone_number.*
import java.util.*

/**
 * Created by subram13 on 1/2/18.
 */
class ResetPwdCodeRequestFragmentView : BaseFragmentView(), IResetPwdCodeRequestFragmentView {
    override fun showInvalidUserIDError(show: Boolean) {
        userID.setError(getString(R.string.forgot_password_email_error))
        userID.showErrorMsg(show)
    }

    override fun onResetPinSent(isEmail: Boolean, userID: String) {
        (activity as CodeRequestListener).onResetPinSent(isEmail, userID)
    }

    override fun onSaveBtnClick() {
        mResetPwdCodeRequestPresenter.handleSendBtnClick(userID.text.toString())
    }

    override fun showUserIDFormatError(show: Boolean) {
        userID.setError(getString(R.string.account_creation_invalid_format))
        userID.showErrorMsg(show)
    }

    override fun showEmailIDFormatError(show: Boolean) {
        userID.setError(getString(R.string.account_creation_invalid_email))
        userID.showErrorMsg(show)
    }

    override fun enableSaveBtn(enable: Boolean, userID: String) {
        (activity as CodeRequestListener).enableActionBtn(enable)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_forgot_password_enter_phone_number, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
       // userID.addTextChangedListener(PhoneNumberFormattingTextWatcher(Locale.getDefault().country))
        userID.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mResetPwdCodeRequestPresenter.validateUserID(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        mResetPwdCodeRequestPresenter = ResetPwdCodeRequestPresenterImpl(this)
    }

    override fun onStart() {
        super.onStart()
        (activity as CodeRequestListener).setTitle(getString(R.string.forgot_password_title))
        (activity as CodeRequestListener).enableActionBtn(false)
    }

    private lateinit var mResetPwdCodeRequestPresenter: IResetPwdCodeRequestPresenter

    interface CodeRequestListener {
        fun enableActionBtn(enable: Boolean)
        fun onResetPinSent(isEmail: Boolean, userID: String)
        fun setTitle(title: String)
    }

    companion object {
        const val TAG = "ResetPwdCodeRequestFragmentView"
    }
}