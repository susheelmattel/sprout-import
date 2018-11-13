package com.sproutling.common.ui.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class PairingViewPagerAdapter(instructionList: ArrayList<View>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        container?.addView(mInstructionList[position], 0)
        return mInstructionList[position]
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mInstructionList.size
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as RelativeLayout)
    }

    private var mInstructionList = instructionList
}