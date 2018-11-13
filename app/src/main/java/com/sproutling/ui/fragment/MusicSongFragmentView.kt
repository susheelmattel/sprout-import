package com.sproutling.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sproutling.R
import com.sproutling.utils.Utils
import com.wx.wheelview.adapter.ArrayWheelAdapter
import com.wx.wheelview.widget.WheelView

/**
 * Created by subram13 on 3/1/18.
 */
class MusicSongFragmentView : Fragment() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mWheelView = view!!.findViewById(R.id.wheelViewList)
        mWheelView.setWheelAdapter(ArrayWheelAdapter(activity))
        mWheelView.setLoop(false)
        mWheelView.setSkin(WheelView.Skin.Holo)
        mWheelView.style = Utils.getWheelViewStyle(activity)
        mWheelView.setOnWheelItemSelectedListener { position, t ->
            mSelectedItem = t
            mSelectedItemPosition = position
        }
        init()
    }

    public fun getSelectedMusic(): String {
        return mWheelView.selectionItem
    }

    private fun init() {
        var type = arguments?.getString(TYPE, PLAYLISTS)
        var items = ArrayList<String>()
        when (type) {
            PLAYLISTS -> {
                items.addAll(resources.getStringArray(R.array.music_playlist))
            }
            MUSIC -> {
                items.addAll(resources.getStringArray(R.array.music_string_list))
            }
            SOUND -> {
                items.addAll(resources.getStringArray(R.array.sound_string_list))
            }
        }
        mWheelView.setWheelData(items)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater?.inflate(R.layout.fragment_music_song, container, false)
        return view!!
    }


    private lateinit var mWheelView: WheelView<String>
    var mSelectedItemPosition: Int = -1
    var mSelectedItem: String? = null

    companion object {
        public fun getInstance(type: String): MusicSongFragmentView {
            var musicSongFragment = MusicSongFragmentView()
            var arguments = Bundle()
            arguments.putString(TYPE, type)
            musicSongFragment.arguments = arguments
            return musicSongFragment
        }

        public const val PLAYLISTS = "Playlists"
        public const val MUSIC = "Music"
        public const val SOUND = "Sound"
        public const val TYPE = "TYPE"
    }
}