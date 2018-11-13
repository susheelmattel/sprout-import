package com.sproutling.common.ui.presenter

import com.sproutling.api.SproutlingApi
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ui.presenter.BaseSignInPresenterImpl.Companion.PASSWORD_MAX
import com.sproutling.common.ui.presenter.BaseSignInPresenterImpl.Companion.PASSWORD_MIN
import com.sproutling.common.ui.presenter.interfaces.IAccountCreationPresenter
import com.sproutling.common.ui.view.AccountCreationFragmentView
import com.sproutling.common.ui.view.interfaces.IAccountCreationFragmentView
import com.sproutling.common.utils.AccountManagement
import com.sproutling.common.utils.CommonConstant
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by subram13 on 1/9/18.
 */
class AccountCreationPresenterImpl(accountCreationFragmentView: IAccountCreationFragmentView) : BaseFrgPresenterImpl(accountCreationFragmentView), IAccountCreationPresenter {
    override fun handleOnStart(viewType: String) {
        mAccountCreationFragmentView.setToolBarTitle(if (viewType == AccountCreationFragmentView.VIEW_TYPE_INFO) {
            BaseApplication.sInstance!!.getString(R.string.my_information)
        } else {
            BaseApplication.sInstance!!.getString(R.string.account_creation_title)
        })
    }


    override fun updateAccount(firstName: String, lastName: String, email: String, phone: String, password: String) {
        val phoneVal = Utils.prefixCountryCode(phone)
        var userAccountInfo = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccountInfo
        var pwd = AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).readPassword()
        var updateRequestBody: UpdateUserRequestBody
        if (password == pwd) {
            if (userAccountInfo.firstName != firstName || userAccountInfo.lastName != lastName || userAccountInfo.phoneNumber != phoneVal || userAccountInfo.email != email) {
                updateRequestBody = UpdateUserRequestBody(firstName, lastName, email, phoneVal)
                updateUserInfo(updateRequestBody, userAccountInfo.id, "")
            } else {
                mAccountCreationFragmentView.onUserInfoUpdated()
            }
        } else {
            updateRequestBody = UpdateUserRequestBody(firstName, lastName, email, phoneVal, userAccountInfo.inviteToken, password, password)
            updateUserInfo(updateRequestBody, userAccountInfo.id, password)
        }
    }

    private fun updateUserInfo(updateUserRequestBody: UpdateUserRequestBody, userID: String, pwdChange: String) {
        mAccountCreationFragmentView.showProgressBar(true)
        SproutlingApi.updateUser(updateUserRequestBody, object : Callback<CreateUserResponse> {
            override fun onFailure(call: Call<CreateUserResponse>?, t: Throwable?) {
                mAccountCreationFragmentView.showProgressBar(false)
                mAccountCreationFragmentView.showGenericErrorDialog()
            }

            override fun onResponse(call: Call<CreateUserResponse>?, response: Response<CreateUserResponse>?) {
                mAccountCreationFragmentView.showProgressBar(false)
                if (response!!.isSuccessful) {
                    AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).saveUserAccountInfo(response.body())
                    if (pwdChange.isNotEmpty())
                        AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).writePassword(pwdChange)
                    mAccountCreationFragmentView.onUserInfoUpdated()
                } else {
                    mAccountCreationFragmentView.showGenericErrorDialog()
                }
            }
        }, userID)
    }

    override fun loadUserValues() {
        mAccountCreationFragmentView.enableActionBtn(false)
        var userAccountInfo = AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).userAccountInfo
        var pwd = AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).readPassword()
        if (userAccountInfo == null) {
            var userAccount = AccountManagement.getInstance(BaseApplication.sInstance!!.getAppContext()).userAccount
            mAccountCreationFragmentView.showProgressBar(true)
            SproutlingApi.getUserInfo(object : Callback<CreateUserResponse> {
                override fun onFailure(call: Call<CreateUserResponse>?, t: Throwable?) {
                    mAccountCreationFragmentView.showProgressBar(false)
                }

                override fun onResponse(call: Call<CreateUserResponse>?, response: Response<CreateUserResponse>?) {
                    mAccountCreationFragmentView.showProgressBar(false)
                    if (response!!.isSuccessful) {
                        AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).saveUserAccountInfo(response.body())
                        mAccountCreationFragmentView.setUserInformation(response.body()!!, pwd)
                    }
                }
            }, userAccount.accessToken, userAccount.resourceOwnerId)
        } else {
            mAccountCreationFragmentView.setUserInformation(userAccountInfo, pwd)

        }
    }

    override fun createAccount(firstName: String, lastName: String, email: String, phone: String, password: String, accountType: String,
                               inviteToken: String, language: String, locale: String, sourceApp: String) {
        mAccountCreationFragmentView.enableActionBtn(false)
        val phoneVal = Utils.prefixCountryCode(phone)
        val requestBody = CreateUserRequestBody(email, firstName, lastName, phoneVal, inviteToken, password, password, accountType, language, locale, sourceApp)
        mAccountCreationFragmentView.showProgressBar(true)
        SproutlingApi.createUser(requestBody, object : Callback<CreateUserResponse> {
            override fun onFailure(call: Call<CreateUserResponse>?, t: Throwable?) {
                mAccountCreationFragmentView.showProgressBar(false)
            }

            override fun onResponse(call: Call<CreateUserResponse>?, response: Response<CreateUserResponse>?) {
                if (response!!.isSuccessful) {
                    AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).saveUserAccountInfo(response.body())
                    loginIdentity(email = email, password = password)
                } else {
                    mAccountCreationFragmentView.showProgressBar(false)
                    if (response.code() == CommonConstant.ERROR_CODE_400) {
                        if (response.errorBody() != null) {
                            val errorString = Utils.convertByteStreamToString(response.errorBody()!!.byteStream())
                            val errorBody = Utils.toObjectFromJson(ErrorBody::class.java, errorString)
                            if (errorBody.errors[0].logref == 5006) {
                                if (errorBody.errors[0].path == "email") {
                                    mEmailState = STATE_FAIL3
                                    mAccountCreationFragmentView.showEmailError(true, R.string.account_creation_email_already_registered)
                                } else if (errorBody.errors[0].path == "phone_number") {
                                    mAccountCreationFragmentView.showPhoneError(true, R.string.account_creation_phone_already_registered)
                                    mPhoneState = STATE_FAIL3
                                }
                            }
                        } else {
                            mAccountCreationFragmentView.showGenericErrorDialog()
                        }

                    } else if (response.code() == CommonConstant.ERROR_CODE_422) {
                        val errorString = Utils.convertByteStreamToString(response.errorBody()!!.byteStream())
                        val errorBody = Utils.toObjectFromJson(ErrorBody::class.java, errorString)
                        if (errorBody.errors[0].logref == 5007) {
                            if (errorBody.errors[0].path == "phone_number") {
                                mAccountCreationFragmentView.showPhoneError(true, R.string.account_creation_invalid_phone)
                                mPhoneState = STATE_FAIL3
                            }
                        }
                    } else {
                        mAccountCreationFragmentView.showGenericErrorDialog()
                    }
                }
            }

        })
    }

    private fun loginIdentity(email: String, password: String) {
        val requestBody = LoginRequestBody(email, password)
        mAccountCreationFragmentView.showProgressBar(true)
        SproutlingApi.login(requestBody, object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                mAccountCreationFragmentView.showProgressBar(false)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).saveUserAccount(loginResponse)
                    AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).writePassword(password)
                    mAccountCreationFragmentView.onUserLoggedIn()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mAccountCreationFragmentView.showProgressBar(false)
            }
        })
    }

    override fun onFirstNameChanged(firstName: String) {
        mFirstNameState = if (firstName.isNotBlank()) STATE_PASS else STATE_FAIL1
        mAccountCreationFragmentView.enableActionBtn(isStatesAllPass())
    }

    override fun onLastNameChanged(lastName: String) {
        mLastNameState = if (lastName.isNotBlank()) STATE_PASS else STATE_FAIL1
        mAccountCreationFragmentView.enableActionBtn(isStatesAllPass())
    }

    override fun onEmailChanged(email: String) {
        mEmailState = if (email.isBlank()) {
            STATE_FAIL2
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            STATE_FAIL1
        } else {
            STATE_PASS
        }
        mAccountCreationFragmentView.enableActionBtn(isStatesAllPass())
    }

    override fun onPhoneChanged(phone: String) {
        mPhoneState = if (phone.isBlank()) {
            STATE_FAIL2
        } else if (!Utils.isPhoneValid(phone)) {
            STATE_FAIL1
        } else {
            STATE_PASS
        }
        mAccountCreationFragmentView.enableActionBtn(isStatesAllPass())
    }

    override fun onPasswordChanged(password: String) {
        val length = password.length
        mPasswordState = when {
            length == 0 -> STATE_FAIL3
            length < PASSWORD_MIN -> STATE_FAIL2
            length > PASSWORD_MAX -> STATE_FAIL1
            else -> STATE_PASS
        }
        mAccountCreationFragmentView.enableActionBtn(isStatesAllPass())
    }

    override fun onFirstNameFocusChanged(firstName: String, hasFocus: Boolean) {
        if (!hasFocus) {
            mAccountCreationFragmentView.showFirstNameError(mFirstNameState == STATE_FAIL1)
        }
    }

    override fun onLastNameFocusChanged(lastName: String, hasFocus: Boolean) {
        if (!hasFocus) {
            mAccountCreationFragmentView.showLastNameError(mLastNameState == STATE_FAIL1)
        }
    }

    override fun onEmailFocusChanged(email: String, hasFocus: Boolean) {
        if (!hasFocus) {
            when (mEmailState) {
                STATE_FAIL1 -> {
                    mAccountCreationFragmentView.showEmailError(true, R.string.account_creation_invalid_email)
                }
                STATE_FAIL2 -> {
                    mAccountCreationFragmentView.showEmailError(true, R.string.account_creation_email_required)
                }
                STATE_FAIL3 -> {
                    mAccountCreationFragmentView.showEmailError(true, R.string.account_creation_email_already_registered)
                }
                else -> {
                    mAccountCreationFragmentView.showEmailError(false, R.string.account_creation_invalid_email)
                }
            }
        }
    }

    override fun onPhoneFocusChanged(phone: String, hasFocus: Boolean) {
        mAccountCreationFragmentView.showPhoneInfoMsg(hasFocus)
        if (!hasFocus) {
            when (mPhoneState) {
                STATE_FAIL1 -> {
                    mAccountCreationFragmentView.showPhoneError(true, R.string.account_creation_invalid_phone)
                }
                STATE_FAIL2 -> {
                    mAccountCreationFragmentView.showPhoneError(true, R.string.account_creation_phone_required)
                }
                STATE_FAIL3 -> {
                    mAccountCreationFragmentView.showPhoneError(true, R.string.account_creation_phone_already_registered)
                }
                else -> {
                    mAccountCreationFragmentView.showPhoneError(false, R.string.account_creation_invalid_email)
                }
            }
        }
    }

    override fun onPasswordFocusChanged(password: String, hasFocus: Boolean) {
        mAccountCreationFragmentView.showPasswordInfoMsg(hasFocus)
        if (!hasFocus) {
            when (mPasswordState) {
                STATE_FAIL1 -> {
                    mAccountCreationFragmentView.showPasswordError(true, R.string.account_creation_password_too_long)
                }
                STATE_FAIL2 -> {
                    mAccountCreationFragmentView.showPasswordError(true, R.string.account_creation_password_too_short)
                }
                STATE_FAIL3 -> {
                    mAccountCreationFragmentView.showPasswordError(true, R.string.account_creation_password_required)
                }
                else -> {
                    mAccountCreationFragmentView.showPasswordError(false, R.string.account_creation_password_required)
                }
            }
        }
    }

    override fun onPasswordChangeClick() {
        mAccountCreationFragmentView.showResetPasswordScreen()
    }

    private fun isStatesAllPass(): Boolean {
        return mFirstNameState == STATE_PASS &&
                mLastNameState == STATE_PASS &&
                mEmailState == STATE_PASS &&
                mPhoneState == STATE_PASS &&
                mPasswordState == STATE_PASS
    }


    private val STATE_PASS = 1
    private val STATE_FAIL1 = 0
    private val STATE_FAIL2 = -1
    private val STATE_FAIL3 = -2
    private var mFirstNameState = STATE_FAIL1
    private var mLastNameState = STATE_FAIL1
    private var mEmailState = STATE_FAIL1
    private var mPhoneState = STATE_FAIL1
    private var mPasswordState = STATE_FAIL1
    private val mAccountCreationFragmentView = accountCreationFragmentView

}