package com.sproutling.ui.activity

import android.os.Bundle
import com.sproutling.R

/**
 * Created by subram13 on 12/14/17.
 */
class StyleGuideSample : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_style_guide)
    }

    override fun onStart() {
        super.onStart()
        setUpToolbar()
        initActionBar()
        setToolBarTitle(getString(R.string.style_guide))
        setActionMenuTitle("    ")
        setActionButtonEnable(false)
        setBackDrawable(R.drawable.ic_android_back_white)
    }
}