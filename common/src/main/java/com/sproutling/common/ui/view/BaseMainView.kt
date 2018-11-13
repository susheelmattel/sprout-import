package com.sproutling.common.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.sproutling.common.R
import com.sproutling.common.ui.dialogfragment.SoftwareUpdateDialogFragment
import com.sproutling.common.ui.presenter.interfaces.IBaseMainPresenter
import com.sproutling.common.ui.view.interfaces.IBaseMainView
import com.sproutling.common.utils.Utils
import com.sproutling.pojos.CreateHandheldResponse
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Created by subram13 on 12/14/17.
 */
abstract class BaseMainView : BaseView(), IBaseMainView, SoftwareUpdateDialogFragment.OnSoftwareUpdateListener {
    companion object {
        const val TAG = "BaseMainView"
        const val ALPHA_ANIMATION_DURATION = 1000L
    }

    private lateinit var mZoomOutAnimation: Animation
    private lateinit var mBaseMainPresenter: IBaseMainPresenter
    private val mAnimationImageList = ArrayList<Int>()
    private var mImageCounter = 0
    private val mAlphaAnimation = AlphaAnimation(1f, 0.7f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splashLayout.setBackgroundColor(Utils.getColor(this,getSplashBackground()))
        logo.setImageResource(getAppLogo())
        message.text = getAppTagLine()
        imgSplash.setImageResource(getSplashImage())
        mBaseMainPresenter = getBaseMainPresenterImpl()
        btnSignUp.setOnClickListener { mBaseMainPresenter.handleSignUpClick() }
        tvSignIn.setOnClickListener { mBaseMainPresenter.handleSignInClick() }
        mZoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.image_zoom_out)
        mAnimationImageList.addAll(getBackgroundImageList())
        imgBackground.animation = mZoomOutAnimation
        imgBackground.setImageResource(getNextDrawable())
        mAlphaAnimation.duration = ALPHA_ANIMATION_DURATION
        mBaseMainPresenter.handleOnLoad()
    }

    abstract fun getSplashBackground(): Int

    private fun getNextDrawable(): Int {
        val size = mAnimationImageList.size
        if (mImageCounter == Integer.MAX_VALUE) {
            mImageCounter = 0
        }
        if (mImageCounter < size) {
            return mAnimationImageList[mImageCounter++]
        } else {
            Log.d(TAG, "Animation imageCounter : " + mImageCounter.toString())
            val index = mImageCounter++ % size
            Log.d(TAG, "Animation image index : " + index.toString())
            return mAnimationImageList[index]
        }

    }

    override fun startUIAnimations() {
        startZoomOutAnimation()
    }

    private fun startZoomOutAnimation() {
        mZoomOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                Log.d(TAG, "onAnimationStart")
            }

            override fun onAnimationEnd(animation: Animation) {
                startAlphaAnimation()
                Log.d(TAG, "onAnimationEnd")
            }

            override fun onAnimationRepeat(animation: Animation) {
                Log.d(TAG, "onAnimationRepeat")
            }
        })

        imgBackground.startAnimation(mZoomOutAnimation)
    }

    private fun startAlphaAnimation() {
        mAlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                imgBackground.setImageResource(getNextDrawable())
                imgBackground.alpha = 1f
                startZoomOutAnimation()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        imgBackground.startAnimation(mAlphaAnimation)
    }

    override fun showSplashScreen(show: Boolean) {
        splashLayout.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showUpdateDialog(updateUrl: String) {
        SoftwareUpdateDialogFragment.newInstance(updateUrl).show(supportFragmentManager, null)
    }

    override fun showSignInUI() {
        openActivity(getSignInView(), null, false)
        //TODO:move this logic to presenter to make this call
        unRegisterEventBus()
    }

    override fun onUpdateClicked(url: String) {
        mBaseMainPresenter.handleOnUpdateClick(url)
    }

    override fun openUpdateUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
        finish()
    }

    override fun onPushNotificationRegistrationSuccess(createHandheldResponse: CreateHandheldResponse?) {
        super.onPushNotificationRegistrationSuccess(createHandheldResponse)
        mBaseMainPresenter.handleOnPushNotificationTokenRegistration()
    }

    override fun networkAvailable() {
        mBaseMainPresenter.handleOnLoad()
    }

    override fun showWelcomeModalDialog() {

    }

    abstract fun getBackgroundImageList(): ArrayList<Int>
    abstract fun getSplashImage(): Int
    abstract fun getAppLogo(): Int
    abstract fun getAppTagLine(): String
    //    abstract fun getButtonDrawable(): Drawable
    abstract fun getBaseMainPresenterImpl(): IBaseMainPresenter

    abstract fun getSignInView(): Class<out BaseSignInView>
}