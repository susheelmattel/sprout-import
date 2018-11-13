package com.sproutling.common.ui.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ui.dialogfragment.GenericErrorDialogFragment
import com.sproutling.common.ui.presenter.AccountCreationPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IAccountCreationPresenter
import com.sproutling.common.ui.view.interfaces.IAccountCreationFragmentView
import com.sproutling.common.utils.AccountManagement
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.CreateUserRequestBody
import com.sproutling.pojos.CreateUserResponse
import kotlinx.android.synthetic.main.fragment_setup_account_creation.*
import java.util.*

/**
 * Created by subram13 on 1/9/18.
 */
class AccountCreationFragmentView : BaseFragmentView(), IAccountCreationFragmentView {
    override fun setToolBarTitle(title: String) {
        (activity as AccountCreationListener).setToolBarTitle(title)
    }

    override fun onUserInfoUpdated() {
        activity.finish()
    }

    override fun onSaveBtnClick() {
        mAccountCreationPresenter.updateAccount(firstName = firstName.text.toString(), lastName = lastName.text.toString(),
                email = email.text.toString(), phone = phone.text.toString(), password = password.text.toString())
    }

    override fun setUserInformation(userAccountInfo: CreateUserResponse, pwd: String) {
        firstName.setText(userAccountInfo.firstName, TextView.BufferType.EDITABLE)
        lastName.setText(userAccountInfo.lastName, TextView.BufferType.EDITABLE)
        email.setText(userAccountInfo.email, TextView.BufferType.EDITABLE)
        phone.setText(userAccountInfo.phoneNumber, TextView.BufferType.EDITABLE)
        password.setText(pwd, TextView.BufferType.EDITABLE)
    }

    override fun onUserLoggedIn() {
        (activity as AccountCreationListener).onAccountCreated()
    }

    override fun onNextBtnClick() {
        mAccountCreationPresenter.createAccount(firstName = firstName.text.toString(), lastName = lastName.text.toString(),
                email = email.text.toString(), phone = phone.text.toString(), password = password.text.toString(), accountType = CreateUserRequestBody.GUARDIAN,
                inviteToken = "", language = Utils.getCurrentLanguage(), locale = Utils.getCurrentLocale().toString(), sourceApp = SOURCE_APP_CARTWHEEL)
    }

    override fun enableActionBtn(enable: Boolean) {
        (activity as AccountCreationListener).enableActionBtn(enable)
    }

    override fun showFirstNameError(show: Boolean) {
        firstName.showErrorMsg(show)
    }

    override fun showLastNameError(show: Boolean) {
        lastName.showErrorMsg(show)
    }

    override fun showEmailError(show: Boolean, error: Int) {
        email.setError(getString(error))
        email.showErrorMsg(show)
    }

    override fun showPhoneError(show: Boolean, error: Int) {
        phone.setError(getString(error))
        phone.showErrorMsg(show)
    }

    override fun showPasswordError(show: Boolean, error: Int) {
        password.setError(getString(error))
        password.showErrorMsg(show)
    }

    override fun showPasswordInfoMsg(show: Boolean) {
        passwordWrapper.showInfoMsg(show)
    }

    override fun showPhoneInfoMsg(show: Boolean) {
        phoneWrapper.showInfoMsg(show)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_setup_account_creation, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstName.addTextChangedListener(mFirstNameTextWatcher)
        lastName.addTextChangedListener(mLastNameTextWatcher)
        email.addTextChangedListener(mEmailTextWatcher)
        phone.addTextChangedListener(mPhoneTextWatcher)
        password.addTextChangedListener(mPasswordTextWatcher)
        phone.addTextChangedListener(PhoneNumberFormattingTextWatcher(Locale.getDefault().country))

        firstName.onFocusChangeListener = mOnFocusChangeListener
        lastName.onFocusChangeListener = mOnFocusChangeListener
        email.onFocusChangeListener = mOnFocusChangeListener
        phone.onFocusChangeListener = mOnFocusChangeListener
        password.onFocusChangeListener = mOnFocusChangeListener

        mAccountCreationPresenter = AccountCreationPresenterImpl(this)
        mViewType = arguments.getString(VIEW_TYPE)
        if (mViewType == VIEW_TYPE_INFO) {
            mAccountCreationPresenter.loadUserValues()
        }

        if(AccountManagement.getInstance(BaseApplication.sInstance?.applicationContext).userAccount != null){
            changePasswordLayout.visibility = View.VISIBLE
            passwordWrapper.visibility = View.GONE
        } else{
            changePasswordLayout.visibility = View.GONE
            passwordWrapper.visibility = View.VISIBLE
        }
        changePasswordLayout.setOnClickListener(View.OnClickListener { mAccountCreationPresenter.onPasswordChangeClick() })

    }

    override fun onStart() {
        super.onStart()
        mAccountCreationPresenter.handleOnStart(mViewType)
    }

    interface AccountCreationListener : BaseFragmentListener {
        fun onAccountCreated()
    }

    private val mFirstNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            mAccountCreationPresenter.onFirstNameChanged(s.toString())

        }
    }
    private val mLastNameTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            mAccountCreationPresenter.onLastNameChanged(s.toString())

        }
    }
    private val mEmailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            mAccountCreationPresenter.onEmailChanged(s.toString())

        }
    }
    private val mPhoneTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            mAccountCreationPresenter.onPhoneChanged(s.toString())

        }
    }
    private val mPasswordTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            mAccountCreationPresenter.onPasswordChanged(s.toString())

        }
    }
    private val mOnFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->

        when (v.id) {
            R.id.firstName -> {
                mAccountCreationPresenter.onFirstNameFocusChanged(firstName = firstName.text.toString(), hasFocus = hasFocus)
            }
            R.id.lastName -> {
                mAccountCreationPresenter.onLastNameFocusChanged(lastName = lastName.text.toString(), hasFocus = hasFocus)
            }
            R.id.email -> {
                mAccountCreationPresenter.onEmailFocusChanged(email = email.text.toString(), hasFocus = hasFocus)
            }
            R.id.phone -> {
                mAccountCreationPresenter.onPhoneFocusChanged(phone = phone.text.toString(), hasFocus = hasFocus)
            }
            R.id.password -> {
                mAccountCreationPresenter.onPasswordFocusChanged(password = password.text.toString(), hasFocus = hasFocus)
            }
        }
    }

    override fun showResetPasswordScreen(){
        (activity as BaseView).replaceFragmentView(SettingsPasswordFragment.newInstance(), SettingsPasswordFragment.TAG, true)

    }

    private lateinit var mAccountCreationPresenter: IAccountCreationPresenter
    private lateinit var mViewType: String

    companion object {
        const val TAG = "AccountCreationFragmentView"
        const val SOURCE_APP_CARTWHEEL = "Cartwheel"
        const val VIEW_TYPE = "VIEW_TYPE"
        const val VIEW_TYPE_SET_UP = "VIEW_TYPE_SET_UP"
        const val VIEW_TYPE_INFO = "VIEW_TYPE_INFO"
        fun getInstance(viewType: String): AccountCreationFragmentView {
            var bundle = Bundle()
            bundle.putString(VIEW_TYPE, viewType)
            var accountCreationFragmentView = AccountCreationFragmentView()
            accountCreationFragmentView.arguments = bundle
            return accountCreationFragmentView
        }
    }
}