package com.sproutling.ui.fragment

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sproutling.R
import com.sproutling.utils.Utils
import com.wx.wheelview.adapter.ArrayWheelAdapter
import com.wx.wheelview.widget.WheelView
import kotlinx.android.synthetic.main.timer_bottom_sheet_dialog.*
import java.util.*

/**
 * Created by subram13 on 3/12/18.
 */

class TimerBottomSheetFragment : BottomSheetDialogFragment() {
    private var mHourWheelView: WheelView<String>? = null
    private var mMinWheelView: WheelView<String>? = null
    var mTimerListener: TimerListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.timer_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val titleText = arguments.getString(TITLE)
        title!!.text = titleText
        reset!!.setOnClickListener { }
        mHourWheelView = view.findViewById(R.id.hoursWheelView)
        mMinWheelView = view.findViewById(R.id.minsWheelView)

        mHourWheelView!!.setWheelAdapter(ArrayWheelAdapter(activity))
        mHourWheelView!!.setLoop(false)
        mHourWheelView!!.skin = WheelView.Skin.Holo

        mMinWheelView!!.setWheelAdapter(ArrayWheelAdapter(activity))
        mMinWheelView!!.setLoop(false)
        mMinWheelView!!.skin = WheelView.Skin.Holo

        mHourWheelView!!.style = Utils.getWheelViewStyle(activity)
        mMinWheelView!!.style = Utils.getWheelViewStyle(activity)

        val hourList = ArrayList<String>()
        hourList.addAll(getHoursList())
        mHourWheelView!!.setWheelData(hourList)
        val minList = ArrayList<String>()
        minList.addAll(getMinsList())
        mMinWheelView!!.setWheelData(minList)
        btnSelect.setOnClickListener {
            if (mTimerListener != null) mTimerListener!!.onSelectedClickListener(getSelectedTimeInMins())
            dismiss()
        }
        reset.setOnClickListener {
            if (mTimerListener != null) mTimerListener!!.onResetClickListener()
            dismiss()
        }
    }

    private fun getSelectedTimeInMins(): Int {
        val selectedMins = mMinWheelView!!.selectionItem as String
        val selectedHours = mHourWheelView!!.selectionItem as String
        val mins = selectedMins.split(" ")[0].toInt()
        val hours = selectedHours.split(" ")[0].toInt()
        val totalMins = mins + (hours * 60)
        return totalMins
    }

    private fun getHoursList(): ArrayList<String> {
        val hours = ArrayList<String>()
        hours.add(getString(R.string.hour, 0))
        hours.add(getString(R.string.hour, 1))
        hours.add(getString(R.string.hours, 2))
        hours.add(getString(R.string.hours, 3))
        hours.add(getString(R.string.hours, 4))
        hours.add(getString(R.string.hours, 5))
        hours.add(getString(R.string.hours, 6))
        hours.add(getString(R.string.hours, 7))
        hours.add(getString(R.string.hours, 8))
        hours.add(getString(R.string.hours, 9))
        hours.add(getString(R.string.hours, 10))
        hours.add(getString(R.string.hours, 11))
        hours.add(getString(R.string.hours, 12))
        hours.add(getString(R.string.hours, 13))
        hours.add(getString(R.string.hours, 14))
        hours.add(getString(R.string.hours, 15))
        hours.add(getString(R.string.hours, 16))
        hours.add(getString(R.string.hours, 17))
        hours.add(getString(R.string.hours, 18))
        hours.add(getString(R.string.hours, 19))
        hours.add(getString(R.string.hours, 20))
        hours.add(getString(R.string.hours, 21))
        hours.add(getString(R.string.hours, 22))
        hours.add(getString(R.string.hours, 23))

        return hours
    }

    private fun getMinsList(): ArrayList<String> {
        val mins = ArrayList<String>()
        mins.add(getString(R.string.minute, 0))
        mins.add(getString(R.string.minutes, 5))
        mins.add(getString(R.string.minutes, 10))
        mins.add(getString(R.string.minutes, 15))
        mins.add(getString(R.string.minutes, 20))
        mins.add(getString(R.string.minutes, 25))
        mins.add(getString(R.string.minutes, 30))
        mins.add(getString(R.string.minutes, 35))
        mins.add(getString(R.string.minutes, 40))
        mins.add(getString(R.string.minutes, 45))
        mins.add(getString(R.string.minutes, 50))
        mins.add(getString(R.string.minutes, 55))
        return mins
    }

    interface TimerListener {
        fun onSelectedClickListener(minutes: Int)
        fun onResetClickListener()
    }

    companion object {
        const val TITLE = "TITLE"

        fun getInstance(title: String): TimerBottomSheetFragment {
            val timerFragment = TimerBottomSheetFragment()
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            timerFragment.arguments = bundle
            return timerFragment
        }
    }
}
