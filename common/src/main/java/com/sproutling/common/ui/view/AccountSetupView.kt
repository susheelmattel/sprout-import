package com.sproutling.common.ui.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.sproutling.common.R
import com.sproutling.common.app.BaseApplication
import com.sproutling.common.ui.presenter.AddChildPresenterImpl
import com.sproutling.common.ui.presenter.interfaces.IAccountSetupPresenter
import com.sproutling.common.ui.view.interfaces.IAccountCreationFragmentView
import com.sproutling.common.ui.view.interfaces.IAccountSetupView
import com.sproutling.common.ui.view.interfaces.IAddChildFragmentView
import com.sproutling.common.utils.AccountManagement

/**
 * Created by subram13 on 1/9/18.
 */
abstract class AccountSetupView : BaseView(), IAccountSetupView, AccountCreationFragmentView.AccountCreationListener, AddChildFragmentView.AddChildListener {
    override fun onChildProfileCreatedOrUpdated() {
        mAccountSetupPresenter.onChildProfileCreated()
    }

    override fun loadAddChildFragment() {
        replaceFragmentView(AddChildFragmentView.instance(AddChildPresenterImpl.SETUP), AddChildFragmentView.TAG, false)
    }

    override fun enableActionBtn(enabled: Boolean) {
        mIsActionBtnEnabled = enabled
        invalidateOptionsMenu()
    }

    override fun onAccountCreated() {
        mAccountSetupPresenter.onAccountCreated()
    }

    override fun loadAccountCreationFragment() {
        replaceFragmentView(AccountCreationFragmentView.getInstance(AccountCreationFragmentView.VIEW_TYPE_SET_UP), AccountCreationFragmentView.TAG, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_account_setup)
        mAccountSetupPresenter = getAccountSetupPresenter()

        mIsUserCreated = AccountManagement.getInstance(BaseApplication.sInstance!!.applicationContext).userAccount != null
        if(mIsUserCreated){
            mAccountSetupPresenter.onAccountCreated()
        } else{
            mAccountSetupPresenter.onAccountSetUpLoad()
        }
    }

    override fun onStart() {
        super.onStart()
        if(mIsUserCreated){
            setToolBarTitle(getString(R.string.child_profile_onboarding_title))
        } else{
            setToolBarTitle(getString(R.string.account_creation_title))
        }
    }

    override fun onBackPressed() {
        if(mIsUserCreated){
            // disable back
            return
        }
        super.onBackPressed()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_one_action, menu)
        var menuItemSave = menu!!.getItem(0)
        menuItemSave.isEnabled = mIsActionBtnEnabled
        menuItemSave.title = getString(R.string.account_creation_next)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.menuItemBtn -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment)
                if (fragment != null) {
                    when (fragment.tag) {
                        AccountCreationFragmentView.TAG -> {
                            (fragment as IAccountCreationFragmentView).onNextBtnClick()
                        }
                        AddChildFragmentView.TAG -> {
                            (fragment as IAddChildFragmentView).onNextBtnClick()
                        }
                    }
                }

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            supportFragmentManager.findFragmentById(R.id.fragment).onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        supportFragmentManager.findFragmentById(R.id.fragment).onActivityResult(requestCode, resultCode, data)
    }

    abstract fun getAccountSetupPresenter(): IAccountSetupPresenter
    private var mIsActionBtnEnabled = false
    private var mIsUserCreated = false
    private lateinit var mAccountSetupPresenter: IAccountSetupPresenter
}