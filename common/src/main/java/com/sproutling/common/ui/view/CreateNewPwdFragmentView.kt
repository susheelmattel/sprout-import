package com.sproutling.common.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sproutling.common.R
import com.sproutling.common.ui.dialogfragment.PasswordChangeSuccessDialogFragment
import com.sproutling.common.ui.presenter.CreateNewPwdPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.ICreateNewPwdPresenter
import com.sproutling.common.ui.view.interfaces.ICreateNewPwdFragmentView
import kotlinx.android.synthetic.main.fragment_forgot_password_new_password.*

/**
 * Created by subram13 on 1/5/18.
 */
class CreateNewPwdFragmentView : BaseFragmentView(), ICreateNewPwdFragmentView {
    override fun closeForgotPassword() {
        (activity as NewPasswordListener).closeForgotPassword()
    }

    override fun showPwdChangeSuccessDialog() {
        val pwdSuccessDlg = PasswordChangeSuccessDialogFragment()
        pwdSuccessDlg.setOnClickListener(View.OnClickListener {
            pwdSuccessDlg.dismiss()
            mCreateNewPwdPresenter.onPwdSuccessDialogClick()
        })
        pwdSuccessDlg.show(fragmentManager, null)
    }

    override fun onNextBtnClick() {
        mCreateNewPwdPresenter.createNewPassword(userID = mUserID, pin = mPin, password = password.text.toString(), confirmPassword = confirmPassword.text.toString(), isEmail = mIsEmail)
    }

    override fun showPwdError(error: Int, show: Boolean) {
        password.setError(getString(error))
        password.showErrorMsg(show)
    }

    override fun showConfirmPwdError(error: Int, show: Boolean) {
        confirmPassword.setError(getString(error))
        confirmPassword.showErrorMsg(show)
    }

    override fun enableActionBtn(enable: Boolean) {
        (activity as NewPasswordListener).enableActionBtn(enable)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_forgot_password_new_password, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mIsEmail = arguments.getBoolean(IS_EMAIL, false)
        mUserID = arguments.getString(USER_ID)
        mPin = arguments.getString(PIN)

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mCreateNewPwdPresenter.handlePwdTextChange(s.toString(), confirmPassword.text.toString())
            }
        })
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mCreateNewPwdPresenter.handleConfirmPwdTextChange(s.toString(), password.text.toString())
            }
        })
        password.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> mCreateNewPwdPresenter.handleOnPwdFocusChange(hasFocus, password.text.toString()) }
        confirmPassword.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus -> mCreateNewPwdPresenter.handleOnConfirmPwdFocusChange(hasFocus, confirmPassword.text.toString()) }

        mCreateNewPwdPresenter = CreateNewPwdPresenterImpl(this)

        (activity as NewPasswordListener).enableActionBtn(false)
    }

    override fun onStart() {
        super.onStart()
        (activity as NewPasswordListener).setTitle(getString(R.string.set_new_password_title))
    }

    interface NewPasswordListener {
        fun setTitle(title: String)
        fun enableActionBtn(enabled: Boolean)
        fun closeForgotPassword()
    }

    private var mIsEmail = false
    private lateinit var mUserID: String
    private lateinit var mPin: String
    private lateinit var mCreateNewPwdPresenter: ICreateNewPwdPresenter

    companion object {
        const val IS_EMAIL = "IS_EMAIL"
        const val USER_ID = "USER_ID"
        const val PIN = "PIN"
        const val TAG = "CreateNewPwdFragmentView"
        fun instance(userID: String, pin: String, isEmail: Boolean): CreateNewPwdFragmentView {
            val bundle = Bundle()
            bundle.putString(USER_ID, userID)
            bundle.putString(PIN, pin)
            bundle.putBoolean(IS_EMAIL, isEmail)
            val fragment = CreateNewPwdFragmentView()
            fragment.arguments = bundle
            return fragment
        }
    }
}