package com.sproutling.common.ui.view

import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.fisherprice.smartconnect.api.constants.FPBLEConstants
import com.sproutling.common.R
import com.sproutling.common.ui.adapter.PairingViewPagerAdapter
import com.sproutling.common.ui.widget.PagerDotView
import kotlinx.android.synthetic.main.fragment_view_pager_pairing_layout.*

abstract class BaseBLE2StepPairingFragmentView : BaseBLEPairingFragmentView() {
    override fun getPeripheralType(): FPBLEConstants.CONNECT_PERIPHERAL_TYPE {
        return FPBLEConstants.CONNECT_PERIPHERAL_TYPE.SEAHORSE
    }

    override fun initViews(view: View) {
        mInstructionList = ArrayList()
        val instructionView1 = activity.layoutInflater.inflate(R.layout.layout_2_step_pairing_instruction, null)
        val instructionView2 = activity.layoutInflater.inflate(R.layout.layout_2_step_pairing_instruction, null)
        val imgInstruction1 = instructionView1.findViewById<ImageView>(R.id.devicePairingImg)
        val title1 = instructionView1.findViewById<TextView>(R.id.instructionTitle)
        val msg1 = instructionView1.findViewById<TextView>(R.id.instructionMessage)
        val imgInstruction2 = instructionView2.findViewById<ImageView>(R.id.devicePairingImg)
        val title2 = instructionView2.findViewById<TextView>(R.id.instructionTitle)
        val msg2 = instructionView2.findViewById<TextView>(R.id.instructionMessage)

        title1.text = getString(R.string.pairing_seahorse_heading1)
        msg1.text = getString(getInstructionMessage1())
        imgInstruction1.setImageResource(getImageDrawable1())

        title2.text = getString(R.string.pairing_seahorse_heading2)
        msg2.text = getString(getInstructionMessage2())
        imgInstruction2.setImageResource(getImageDrawable2())

        mInstructionList.add(instructionView1)
        mInstructionList.add(instructionView2)
        var pairingViewPagerAdapter = PairingViewPagerAdapter(mInstructionList)
        pagerInstruction.adapter = pairingViewPagerAdapter
        mPagerDotView = PagerDotView(activity, dot_layout, mInstructionList.size)
        pagerInstruction.addOnPageChangeListener(mOnPageChangeListener)

    }

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            mPagerDotView.setPosition(position)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    abstract fun getImageDrawable2(): Int

    abstract fun getInstructionMessage2(): Int

    abstract fun getImageDrawable1(): Int

    abstract fun getInstructionMessage1(): Int


    override fun getLayout(): Int {
        return R.layout.fragment_view_pager_pairing_layout
    }


    private lateinit var mInstructionList: ArrayList<View>
    private lateinit var mPagerDotView: PagerDotView
}