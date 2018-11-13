package com.sproutling.common.ui.view

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.sproutling.common.R
import com.sproutling.common.ui.presenter.BaseForgotPasswordPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IBaseForgotPasswordPresenter
import com.sproutling.common.ui.view.interfaces.IBaseForgotPasswordView
import com.sproutling.common.ui.view.interfaces.ICreateNewPwdFragmentView
import com.sproutling.common.ui.view.interfaces.IPinVerificationFragmentView
import com.sproutling.common.ui.view.interfaces.IResetPwdCodeRequestFragmentView


/**
 * Created by subram13 on 1/2/18.
 */
class BaseForgotPasswordView : BaseView(), IBaseForgotPasswordView, ResetPwdCodeRequestFragmentView.CodeRequestListener, PinVerificationFragmentView.PinVerifyListener, CreateNewPwdFragmentView.NewPasswordListener {
    override fun closeForgotPassword() {
        finish()
    }

    override fun setTitle(title: String) {
        setToolBarTitle(title)
    }

    override fun loadCreateNewPwd(pin: String, userID: String, isEmail: Boolean) {
        replaceFragmentView(CreateNewPwdFragmentView.instance(userID, pin, isEmail), CreateNewPwdFragmentView.TAG, false)
        invalidateOptionsMenu()
    }

    override fun onCodeAccepted(pin: String, userID: String, isEmail: Boolean) {
        mBaseForgotPasswordPresenter.handlePinVerified(pin, userID, isEmail)
    }

    override fun loadPinVerificationFragment(isEmail: Boolean, userID: String) {
        replaceFragmentView(PinVerificationFragmentView.instance(userID, isEmail), PinVerificationFragmentView.TAG, false)
        invalidateOptionsMenu()
    }

    override fun onResetPinSent(isEmail: Boolean, userID: String) {
        mBaseForgotPasswordPresenter.handleResentPinSent(isEmail, userID)
    }

    override fun loadResetPwdCodeRequestFragment() {
        replaceFragmentView(ResetPwdCodeRequestFragmentView(), ResetPwdCodeRequestFragmentView.TAG, false)
        invalidateOptionsMenu()
    }

    override fun enableActionBtn(enable: Boolean) {
        mIsActionBtnEnabled = enable
        invalidateOptionsMenu()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_forgot_password)
        mBaseForgotPasswordPresenter = BaseForgotPasswordPresenterImpl(this)
        mBaseForgotPasswordPresenter.onForgotPasswordLoad()
    }

    override fun onStart() {
        super.onStart()
        setToolBarTitle(getString(R.string.forgot_password_title))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_one_action, menu)
        var menuItemSave = menu!!.getItem(0)
        val fragmentTag = supportFragmentManager.findFragmentById(R.id.fragment).tag
        val title = when (fragmentTag) {
            ResetPwdCodeRequestFragmentView.TAG -> {
                getString(R.string.forgot_password_send)
            }
            PinVerificationFragmentView.TAG -> {
                getString(R.string.forgot_password_next)
            }
            CreateNewPwdFragmentView.TAG -> {
                getString(R.string.forgot_password_next)
            }
            else -> {
                getString(R.string.forgot_password_send)
            }
        }
        var spannableTitle = SpannableString(title)
        menuItemSave.isEnabled = mIsActionBtnEnabled

        if (mIsActionBtnEnabled) {
            spannableTitle.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 0, spannableTitle.length, 0)
        } else {
            spannableTitle.setSpan(ForegroundColorSpan(Color.GRAY), 0, spannableTitle.length, 0)
        }
        menuItemSave.title = spannableTitle


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.menuItemBtn -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment)
                when (fragment.tag) {
                    ResetPwdCodeRequestFragmentView.TAG -> {
                        (fragment as IResetPwdCodeRequestFragmentView).onSaveBtnClick()
                    }
                    PinVerificationFragmentView.TAG -> {
                        (fragment as IPinVerificationFragmentView).onNextBtnClick()
                    }
                    CreateNewPwdFragmentView.TAG -> {
                        (fragment as ICreateNewPwdFragmentView).onNextBtnClick()
                    }
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private var mIsActionBtnEnabled = false
    private lateinit var mBaseForgotPasswordPresenter: IBaseForgotPasswordPresenter
}